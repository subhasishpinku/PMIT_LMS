package com.online.course.ui.widget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.online.course.R
import com.online.course.databinding.DialogChargeBinding
import com.online.course.databinding.DialogCouponBinding
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.ChargeAccountPaymentPresenterImpl
import com.online.course.presenterImpl.ChargeAccountPresenterImpl
import com.online.course.presenterImpl.CouponPresenterImpl

class ChargeDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogChargeBinding
    private lateinit var mPresenter: Presenter.ChargeAccountPresenter
    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            mBinding.chargeBtn.isEnabled = mBinding.chargeEdtx.text.toString().isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogChargeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = ChargeAccountPresenterImpl(this)

        mBinding.chargeEdtx.addTextChangedListener(mTextWatcher)
        mBinding.chargeBtn.setOnClickListener(this)
        mBinding.chargeCancelBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.chargeCancelBtn -> {
                dismiss()
            }

            R.id.chargeBtn -> {
                val paymentRequest = PaymentRequest()
                try {
                    paymentRequest.amount = mBinding.chargeEdtx.text.toString().toDouble()
                } catch (ex: NumberFormatException) {
                    ToastMaker.show(
                        requireContext(),
                        getString(R.string.error),
                        getString(R.string.enter_valid_number),
                        ToastMaker.Type.ERROR
                    )
                    return
                }

                mPresenter.chargeAccount(paymentRequest)
            }
        }
    }

    fun onCheckout(data: Data<Response>) {
        if (data.isSuccessful && !data.data!!.link.isNullOrEmpty()) {
            dismiss()
            Utils.openLink(requireContext(), data.data!!.link)
        } else {
            if (context == null) return
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }
}