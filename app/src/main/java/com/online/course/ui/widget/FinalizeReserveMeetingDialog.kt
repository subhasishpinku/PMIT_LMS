package com.online.course.ui.widget

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import com.online.course.R
import com.online.course.databinding.DialogFinalizeReserveMeetingBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.Utils.toBoolean
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.BaseResponse
import com.online.course.model.Meeting
import com.online.course.model.ReserveTimeMeeting
import com.online.course.model.Timing
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.FinalizeReserveMeetingPresenterImpl
import kotlin.math.roundToInt


class FinalizeReserveMeetingDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener,
    SeekBar.OnSeekBarChangeListener {

    private lateinit var mBinding: DialogFinalizeReserveMeetingBinding
    private lateinit var mMeetingTimeReserve: ReserveTimeMeeting
    private lateinit var mPresenter: Presenter.FinalizeReserveMeetingPresenter
    private lateinit var mLoadingDialog: LoadingDialog

    private var mConfirmationDialog: AppDialog? = null
    private var mConductionType: String? = null
    private var mMeetingType: String? = null
    private var mSkipMin = false
    private var mLock = false

    companion object {
        private const val TAG = "FinalizeReserveMeetingD"
    }

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogFinalizeReserveMeetingBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mMeetingTimeReserve = requireArguments().getParcelable(App.MEETING)!!
        val timing = mMeetingTimeReserve.time
        val meeting = timing.meeting

        mBinding.finalizeReserveMeetingSelectedTimeTv.text =
            StringBuilder(mMeetingTimeReserve.date).append(" | ")
                .append(mMeetingTimeReserve.time.time)

        val symbol = App.appConfig.currency.sign

        mBinding.finalizeReserveMeetingOnlinePriceTv.text =
            if (meeting.onlinePriceWithDiscount > 0.0)
                Utils.formatPrice(
                    requireContext(),
                    meeting.onlinePriceWithDiscount
                ) else Utils.formatPrice(requireContext(), meeting.onlinePrice)

        mBinding.finalizeReserveMeetingInPersionPriceTv.text =
            if (meeting.inPersonPriceWithDiscount > 0.0)
                Utils.formatPrice(
                    requireContext(),
                    meeting.inPersonPriceWithDiscount
                ) else Utils.formatPrice(requireContext(), meeting.inPersonPrice)

        if (timing.meetingType == Meeting.Type.IN_PERSON.value) {
            mBinding.finalizeReserveMeetingOnlineRateTv.visibility = View.GONE
            mBinding.finalizeReserveMeetingOnlinePriceTv.visibility = View.GONE
        } else if (timing.meetingType == Meeting.Type.IN_PERSON.value) {
            mBinding.finalizeReserveMeetingInPersionRateTv.visibility = View.GONE
            mBinding.finalizeReserveMeetingInPersionPriceTv.visibility = View.GONE
        }

        if (meeting.groupMeeting.toBoolean()) {
            mBinding.finalizeReserveMeetingGroupMeetingEnableTv.visibility = View.VISIBLE
        }

        mPresenter = FinalizeReserveMeetingPresenterImpl(this)

        mBinding.finalizeReserveMeetingDialogAddBtn.setOnClickListener(this)
        mBinding.finalizeReserveMeetingCancelBtn.setOnClickListener(this)

        initSelections()
    }


    private fun initSelections() {
        val timing = mMeetingTimeReserve.time
        val meeting = timing.meeting

        TwoItemSelectionHelper(
            mBinding.finalizeReserveMeetingConductionSelection,
            TwoItemSelectionHelper.Item(
                getString(R.string.in_persion),
                timing.meetingType != Meeting.Type.ONLINE.value
            ),
            TwoItemSelectionHelper.Item(
                getString(R.string.online),
                timing.meetingType != Meeting.Type.IN_PERSON.value
            ),
            object : TwoItemSelectionHelper.ItemSelected {
                override fun onItemSelected(item: TwoItemSelectionHelper.Item) {
                    if (!item.enabled) {
                        ToastMaker.show(
                            requireContext(),
                            getString(R.string.error),
                            getString(R.string.not_available_for_meeting),
                            ToastMaker.Type.ERROR
                        )
                        return
                    }

                    mConductionType = if (item.title == getString(R.string.in_persion)) {
                        Meeting.Type.IN_PERSON.value
                    } else {
                        Meeting.Type.ONLINE.value
                    }

                    if (mMeetingType != null) {
                        updatePrice()
                        if (mMeetingType == getString(R.string.group)) {
                            showGroupData()
                        }
                    }
                }
            }
        )

        TwoItemSelectionHelper(
            mBinding.finalizeReserveMeetingSelection,
            TwoItemSelectionHelper.Item(
                getString(R.string.individual),
            ),
            TwoItemSelectionHelper.Item(
                getString(R.string.group),
                meeting.groupMeeting.toBoolean()
            ),
            object : TwoItemSelectionHelper.ItemSelected {
                override fun onItemSelected(item: TwoItemSelectionHelper.Item) {
                    if (!item.enabled) {
                        ToastMaker.show(
                            requireContext(),
                            getString(R.string.error),
                            getString(R.string.not_available_for_meeting),
                            ToastMaker.Type.ERROR
                        )
                        return
                    }

                    if (mMeetingType != null && mMeetingType == item.title) {
                        return
                    }

                    mMeetingType = item.title

                    if (item.title == getString(R.string.individual)) {
                        mBinding.finalizeReserveMeetingParticipantsTv.visibility = View.GONE
                        mBinding.finalizeReserveMeetingParticipantsSeekBar.visibility = View.GONE
                        mBinding.finalizeReserveMeetingGroupDetailsContainer.visibility = View.GONE
                        mBinding.finalizeReserveMeetingParticipantsCountTv.visibility = View.GONE
                    } else {
                        if (mConductionType != null) {
                            showGroupData()
                        }
                    }

                    if (mConductionType != null) {
                        updatePrice()
                    }
                }
            }
        )
    }

    private fun showGroupData() {
        resetGroupData()
        mBinding.finalizeReserveMeetingParticipantsTv.visibility = View.VISIBLE
        mBinding.finalizeReserveMeetingParticipantsSeekBar.visibility = View.VISIBLE
        mBinding.finalizeReserveMeetingGroupDetailsContainer.visibility =
            View.VISIBLE
    }

    private fun resetGroupData() {
        mBinding.finalizeReserveMeetingParticipantsSeekBar.setOnSeekBarChangeListener(null)

        mSkipMin = false
        val meeting = mMeetingTimeReserve.time.meeting
        val min: Int
        val max: Int

        if (mConductionType == Meeting.Type.ONLINE.value) {
            min = meeting.onlineGroupMinStudent
            max = meeting.onlineGroupMaxStudent
        } else {
            min = meeting.inPersonGroupMinStudent
            max = meeting.inPersonGroupMaxStudent
        }

        mBinding.finalizeReserveMeetingParticipantsSeekBar.tag = min
        mBinding.finalizeReserveMeetingParticipantsSeekBar.max = max
        updateGroupCountTv(min)
        mBinding.finalizeReserveMeetingParticipantsSeekBar.setOnSeekBarChangeListener(this)

        setPrice(false)

        val capacity = "$min-$max"
        mBinding.finalizeReserveMeetingGroupLiveCapacityValTv.text = capacity
    }

    private fun updateGroupCountTv(progress: Int) {
        mBinding.finalizeReserveMeetingParticipantsSeekBar.progress = progress
        Handler(Looper.getMainLooper()).postDelayed({
            onProgressChanged(
                mBinding.finalizeReserveMeetingParticipantsSeekBar,
                progress,
                false
            )
            mBinding.finalizeReserveMeetingParticipantsCountTv.visibility = View.VISIBLE
        }, 2)
    }

    private fun setPrice(belowMin: Boolean) {
        val meeting = mMeetingTimeReserve.time.meeting
        val amount: Double
        amount = if (mConductionType == Meeting.Type.ONLINE.value) {
            if (belowMin) {
                if (meeting.onlinePriceWithDiscount > 0.0)
                    meeting.onlinePriceWithDiscount else meeting.onlinePrice
            } else {
                meeting.onlineGroupAmount
            }
        } else {
            if (belowMin) {
                if (meeting.inPersonPriceWithDiscount > 0.0)
                    meeting.inPersonPriceWithDiscount else meeting.inPersonPrice
            } else {
                meeting.inPersonGroupAmount
            }
        }

        val price = Utils.formatPrice(requireContext(), amount)
        mBinding.finalizeReserveMeetingGroupHourlyPriceTv.text = price
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.finalizeReserveMeetingCancelBtn -> {
                dismiss()
            }

            R.id.finalizeReserveMeetingDialogAddBtn -> {
                mLoadingDialog = LoadingDialog.instance
                mLoadingDialog.show(childFragmentManager, null)
                val description = mBinding.finalizeReserveMeetingDescEdtx.text.toString()

                if (description.isNotEmpty()) {
                    mMeetingTimeReserve.description = description
                }

                if (mMeetingType == getString(R.string.group)) {
                    val participants = mBinding.finalizeReserveMeetingParticipantsSeekBar.progress
                    mMeetingTimeReserve.studentCount = participants
                }

                if (mConductionType == Meeting.Type.ONLINE.value) {
                    mMeetingTimeReserve.meetingType = Meeting.Type.ONLINE.value
                } else {
                    mMeetingTimeReserve.meetingType = Meeting.Type.IN_PERSON.value
                }

                mPresenter.reserveMeeting(mMeetingTimeReserve)
            }
        }
    }

    fun onMeetingReserved(response: BaseResponse) {
        if (context == null) return

        mLoadingDialog.dismiss()

        val title: String
        val type: ToastMaker.Type
        if (response.isSuccessful) {
            title = getString(R.string.success)
            type = ToastMaker.Type.SUCCESS
        } else {
            title = getString(R.string.error)
            type = ToastMaker.Type.ERROR
        }
        ToastMaker.show(requireContext(), title, response.message, type)

        if (response.isSuccessful) {
            (parentFragment as ReserveMeetingDialog).dismiss()
            dismiss()
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (progress < 1) {
            seekBar.progress = 1
            return
        }

        if (progress < seekBar.tag as Int) {
            if (!mSkipMin && !mLock) {
                showConfirmationDialogForBelowMin(progress)
                return
            }
        } else {
            if (mConfirmationDialog != null && mConfirmationDialog!!.isVisible) {
                mLock = false
                mConfirmationDialog!!.dismiss()
            }

            if (mSkipMin) {
                setPrice(false)
                mSkipMin = false
            }
        }

        val thumb = seekBar.thumb
        val params =
            mBinding.finalizeReserveMeetingParticipantsCountTv.layoutParams as LinearLayout.LayoutParams

        params.leftMargin =
            (thumb.bounds.left + thumb.intrinsicWidth / 2 + resources.getDimension(R.dimen.margin_4)).roundToInt()
        mBinding.finalizeReserveMeetingParticipantsCountTv.requestLayout()

        mBinding.finalizeReserveMeetingParticipantsCountTv.text = progress.toString()
        updatePrice()
    }

    private fun updatePrice() {
        val amount: Double = if (mMeetingType == getString(R.string.group)) {
            val participants = mBinding.finalizeReserveMeetingParticipantsSeekBar.progress
            val amountFromMeetingType = getAmountFromMeetingType(participants)
            amountFromMeetingType * participants
        } else {
            getAmountFromConductionType()
        }

        val btnText =
            "${getString(R.string.add_to_cart)} (${Utils.formatPrice(requireContext(), amount)})"
        mBinding.finalizeReserveMeetingDialogAddBtn.text = btnText
        mBinding.finalizeReserveMeetingDialogAddBtn.isEnabled = true
    }

    private fun getAmountFromConductionType(): Double {
        val meeting = mMeetingTimeReserve.time.meeting
        return if (mConductionType == Meeting.Type.ONLINE.value) {
            if (meeting.onlinePriceWithDiscount > 0.0) {
                meeting.onlinePriceWithDiscount
            } else meeting.onlinePrice
        } else {
            if (meeting.inPersonPriceWithDiscount > 0.0) {
                meeting.inPersonPriceWithDiscount
            } else meeting.inPersonPrice
        }
    }

    private fun getAmountFromMeetingType(participants: Int): Double {
        val meeting = mMeetingTimeReserve.time.meeting
        return if (mConductionType == Meeting.Type.ONLINE.value) {
            if (participants < meeting.onlineGroupMinStudent) {
                getAmountFromConductionType()
            } else meeting.onlineGroupAmount
        } else {
            if (participants < meeting.inPersonGroupMinStudent) {
                getAmountFromConductionType()
            } else meeting.inPersonGroupAmount
        }
    }

    private fun showConfirmationDialogForBelowMin(progress: Int) {
        if (mLock) {
            return
        }
        mLock = true

        mConfirmationDialog = AppDialog.instance
        val bundle = Bundle()

        bundle.putString(App.TITLE, getString(R.string.price_change))
        bundle.putString(App.TEXT, getString(R.string.price_below_min))
        mConfirmationDialog!!.arguments = bundle
        mConfirmationDialog!!.setOnDialogBtnsClickedListener(
            AppDialog.DialogType.YES_CANCEL,
            object : AppDialog.OnDialogCreated {

                override fun onCancel() {
                    mLock = false
                    updateGroupCountTv(getMinParticipants())
                }

                override fun onOk() {
                    mLock = false
                    mSkipMin = true
                    setPrice(true)
                    updatePrice()
                    updateGroupCountTv(progress)
                }
            })

        mConfirmationDialog!!.show(childFragmentManager, null)
    }

    private fun getMinParticipants(): Int {
        val meeting = mMeetingTimeReserve.time.meeting
        return if (mConductionType == Meeting.Type.ONLINE.value) {
            meeting.onlineGroupMinStudent
        } else {
            meeting.inPersonGroupMinStudent
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    fun onFailed() {
        mLoadingDialog.dismiss()
    }
}