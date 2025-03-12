package com.online.course.ui.widget

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.google.android.material.datepicker.*
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.online.course.R
import com.online.course.databinding.DialogOfflinePaymentDetailsBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.adapter.ItemSpinnerAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.BaseResponse
import com.online.course.model.OfflinePayment
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.OfflinePaymentDialogPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.BanksInfoFrag
import java.util.*

class OfflinePaymentDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var mBinding: DialogOfflinePaymentDetailsBinding
    private lateinit var mPresenter: Presenter.OfflinePaymentDialogPresenter
    private var mSelectedTimestamp = 0L
    private var mBank: String? = null
    private var mCallback: ItemCallback<Any>? = null

    private val mAmountReferenceTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            enableDisableBtn()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun enableDisableBtn() {
        val amount = mBinding.offlinePaymentDetailsAmountEdtx.text.toString()
        val reference = mBinding.offlinePaymentDetailsReferenceEdtx.text.toString()

        mBinding.offlinePaymentDetailsSubmitBtn.isEnabled =
            amount.isNotEmpty() && reference.isNotEmpty() && mBank != null && mSelectedTimestamp > 0
    }

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
        if (dialog != null) {
            dialog!!.setOnDismissListener { mCallback?.onItem(Any()) }
        }
    }

    override fun dismiss() {
        super.dismiss()
        mCallback?.onItem(Any())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogOfflinePaymentDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = OfflinePaymentDialogPresenterImpl(this)

        val amount = requireArguments().getString(App.AMOUNT)
        mBinding.offlinePaymentDetailsAmountEdtx.setText(amount)

        mBinding.offlinePaymentDetailsAmountEdtx.addTextChangedListener(mAmountReferenceTextWatcher)
        mBinding.offlinePaymentDetailsReferenceEdtx.addTextChangedListener(
            mAmountReferenceTextWatcher
        )

        val adapter = ItemSpinnerAdapter(requireContext(), App.appConfig.offLineBankAccounts)
        mBinding.offlinePaymentDetailsBankSpinner.adapter = adapter
        mBinding.offlinePaymentDetailsBankSpinner.onItemSelectedListener = this

        mBinding.offlinePaymentDetailsDateTimeTv.setOnClickListener(this)
        mBinding.offlinePaymentDetailsSubmitBtn.setOnClickListener(this)
        mBinding.offlinePaymentDetailsBankInfoBtn.setOnClickListener(this)
        mBinding.offlinePaymentDetailsCancelBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.offlinePaymentDetailsDateTimeTv -> {
                showDatePicker()
            }

            R.id.offlinePaymentDetailsSubmitBtn -> {
                val amount = mBinding.offlinePaymentDetailsAmountEdtx.text.toString()
                val reference = mBinding.offlinePaymentDetailsReferenceEdtx.text.toString()

                val offlinePayment = OfflinePayment()
                offlinePayment.amount = amount.toDouble()
                offlinePayment.referenceNumber = reference
                offlinePayment.payDate = mSelectedTimestamp
                offlinePayment.bank = mBank!!

                mPresenter.addOfflinePayment(offlinePayment)
            }

            R.id.offlinePaymentDetailsBankInfoBtn -> {
                (activity as MainActivity).transact(BanksInfoFrag())
            }

            R.id.offlinePaymentDetailsCancelBtn -> {
                dismiss()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)

        val constraintsBuilder = CalendarConstraints.Builder()
        val validators: ArrayList<CalendarConstraints.DateValidator> = ArrayList()
        validators.add(DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds()))
        validators.add(DateValidatorPointForward.from(calendar.timeInMillis))
        constraintsBuilder.setValidator(CompositeDateValidator.allOf(validators))

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.Calendar_Theme)
            .setCalendarConstraints(constraintsBuilder.build())
            .setTitleText(getString(R.string.payment_date)).build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            showTimerPicker(Date(selection))
        }
        datePicker.show(childFragmentManager, null)
    }

    private fun showTimerPicker(date: Date) {
        val timePicker = MaterialTimePicker.Builder()
            .setTheme(R.style.TimePicker_Theme)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(getString(R.string.payment_time)).build()

        timePicker.addOnPositiveButtonClickListener {
            val dateCalender = Calendar.getInstance()
            dateCalender.time = date

            val calendar = Calendar.getInstance()
            calendar.set(
                dateCalender.get(Calendar.YEAR),
                dateCalender.get(Calendar.MONTH),
                dateCalender.get(Calendar.DATE),
                timePicker.hour,
                timePicker.minute
            )

            mSelectedTimestamp = calendar.timeInMillis / 1000
            mBinding.offlinePaymentDetailsDateTimeTv.text =
                Utils.getDateTimeFromTimestamp(mSelectedTimestamp)

            enableDisableBtn()
        }

        timePicker.show(childFragmentManager, null)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mBank = (mBinding.offlinePaymentDetailsBankSpinner.adapter as ItemSpinnerAdapter)
            .items[position]

        enableDisableBtn()
    }

    fun onOfflinePaymentSaved(response: BaseResponse) {
        if (response.isSuccessful) {
            dismiss()
            activity?.onBackPressed()
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun setOnDismissListener(itemCallback: ItemCallback<Any>) {
        mCallback = itemCallback
    }
}