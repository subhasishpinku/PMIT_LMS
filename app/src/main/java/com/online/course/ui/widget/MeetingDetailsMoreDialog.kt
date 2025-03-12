package com.online.course.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.online.course.R
import com.online.course.databinding.DialogMeetingDetailsMoreBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.BaseResponse
import com.online.course.model.Meeting
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.MeetingDetailsMorePresenterImpl
import java.util.*


class MeetingDetailsMoreDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogMeetingDetailsMoreBinding
    private lateinit var mMetting: Meeting
    private lateinit var mPresenter: Presenter.MeetingDetailsMorePresenter

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogMeetingDetailsMoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = MeetingDetailsMorePresenterImpl(this)
        mMetting = requireArguments().getParcelable(App.MEETING)!!
        mBinding.meetingDetailsAddToCalendarTv.setOnClickListener(this)
        mBinding.meetingDetailsMoreFinishMeetingTv.setOnClickListener(this)
        mBinding.meetingDetailsCancelBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.meetingDetailsAddToCalendarTv -> {
                val calendar = Calendar.getInstance()
                calendar.time = Date(mMetting.date * 1000)

                var startTime = mMetting.time.start.split(":").toMutableList()
                var endTime = mMetting.time.end.split(":").toMutableList()

                if (mMetting.time.start.lowercase(Locale.ENGLISH).endsWith("pm")) {
                    if (startTime[0].toInt() != 12) {
                        val start = startTime[0].toInt() + 12
                        mMetting.time.start = "${start}:${startTime[1]}"
                    }

                    mMetting.time.start =
                        mMetting.time.start.lowercase(Locale.ENGLISH).replace("pm", "")
                } else {
                    mMetting.time.start =
                        mMetting.time.start.lowercase(Locale.ENGLISH).replace("am", "")
                }

                if (mMetting.time.end.lowercase(Locale.ENGLISH).endsWith("pm")) {
                    if (endTime[0].toInt() != 12) {
                        val end = endTime[0].toInt() + 12
                        mMetting.time.end = "${end}:${endTime[1]}"
                    }

                    mMetting.time.end =
                        mMetting.time.end.lowercase(Locale.ENGLISH).replace("pm", "")
                } else {
                    mMetting.time.end =
                        mMetting.time.end.lowercase(Locale.ENGLISH).replace("am", "")
                }

                startTime = mMetting.time.start.split(":").toMutableList()
                endTime = mMetting.time.end.split(":").toMutableList()


                if (startTime.size != 2) {
                    startTime[1] = "0"
                }

                if (endTime.size != 2) {
                    endTime[1] = "0"
                }

                val startTimeCalendar = Calendar.getInstance()
                startTimeCalendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    startTime[0].toInt(),
                    startTime[1].toInt()
                )

                val endTimeCalendar = Calendar.getInstance()
                endTimeCalendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    endTime[0].toInt(),
                    endTime[1].toInt()
                )

                Utils.addToCalendar(
                    requireContext(),
                    getString(R.string.meeting_with) + " ${mMetting.user.name}",
                    startTimeCalendar.timeInMillis,
                    endTimeCalendar.timeInMillis
                )
            }

            R.id.meetingDetailsMoreFinishMeetingTv -> {
                val dialog = AppDialog()
                val bundle = Bundle()
                bundle.putString(App.TITLE, getString(R.string.finish))
                bundle.putString(App.TEXT, getString(R.string.finish_meeting_desc))
                dialog.arguments = bundle
                dialog.setOnDialogBtnsClickedListener(AppDialog.DialogType.YES_CANCEL,
                    object : AppDialog.OnDialogCreated {

                        override fun onCancel() {
                        }

                        override fun onOk() {
                            mPresenter.finishMeeting(mMetting.id)
                        }

                    })
                dialog.show(childFragmentManager, null)
            }

            R.id.meetingDetailsCancelBtn -> {
                dismiss()
            }
        }
    }

    fun onMeetingFinished(response: BaseResponse) {
        if (context == null) return

        if (response.isSuccessful) {
            dismiss()
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }
}