package com.online.course.ui.frag

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.FragChargeAccountBinding
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.adapter.PaymentGatewayGridAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.*
import com.online.course.model.view.PaymentRedirection
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.ChargeAccountPaymentPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.LoadingDialog
import com.online.course.ui.widget.OfflinePaymentDialog


class ChargeAccountPaymentFrag : Fragment(), View.OnClickListener,
    PaymentGatewayGridAdapter.Selection {

    private lateinit var mBinding: FragChargeAccountBinding
    private lateinit var mPresenter: Presenter.ChargeAccountPaymentPresenter
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mPaymentRedirection: PaymentRedirection

    private var mCheckout: CheckoutRes? = null

    companion object {
        const val PAYMENT_GATEWAY_URL = "${BuildVars.BASE_URL}panel/payments/request"
    }

    enum class PaymentMethod(val type: Int) {
        ACCOUNT_CHARGE(-1),
        OFFLINE_PAYMENT(-2);
    }

    private val mAmountTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            enableDisableBtn()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    private fun enableDisableBtn() {
        val amount = mBinding.chargeAccountPaymentAmountEdtx.text.toString()
        val selectedItem =
            (mBinding.chargeAccountGatewaysRv.adapter as PaymentGatewayGridAdapter).getSelectedItem()
        mBinding.chargeAccountPaymentPayBtn.isEnabled =
            amount.isNotEmpty() && selectedItem != null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragChargeAccountBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK
        (activity as MainActivity).showToolbar(toolbarOptions, R.string.payment_method)

        mPresenter = ChargeAccountPaymentPresenterImpl(this)
        mPaymentRedirection = requireArguments().getParcelable(App.REDIRECTION)!!

        mBinding.chargeAccountPaymentPayBtn.setOnClickListener(this)

        if (arguments != null) {
            mCheckout = requireArguments().getParcelable(App.ORDER)
        }

        var gateways = mCheckout?.paymentChannels?.toMutableList()
        if (gateways == null) {
            gateways = App.appConfig.activePaymentChannels.paymentChannels.toMutableList()
        }

        var userCharge = 0.0

        if (mCheckout == null) {
            mBinding.chargeAccountPaymentTitleTv.text = getString(R.string.charge_amount)
            mBinding.chargeAccountPaymentAmountEdtx.addTextChangedListener(mAmountTextWatcher)

            val paymentChannel = PaymentChannel()
            paymentChannel.id = -2
            paymentChannel.title = getString(R.string.offline_payment)
            paymentChannel.className = "OfflinePayment"

            gateways.add(paymentChannel)
        } else {
            userCharge = mCheckout!!.userCharge
            mBinding.chargeAccountPaymentTitleTv.text = getString(R.string.total)
            mBinding.chargeAccountPaymentAmountTv.text =
                Utils.formatPrice(requireContext(), mCheckout!!.total)
            mBinding.chargeAccountPaymentAmountEdtx.visibility = View.GONE

            val paymentChannel = PaymentChannel()
            paymentChannel.id = -1
            paymentChannel.title = getString(R.string.account_charge)
            paymentChannel.className = "AccountCharge"

            gateways.add(paymentChannel)
        }

        mBinding.chargeAccountGatewaysRv.adapter =
            PaymentGatewayGridAdapter(
                gateways,
                this,
                mBinding.chargeAccountGatewaysRv,
                userCharge
            )
    }

    override fun onClick(v: View?) {
        val selectedgateway =
            (mBinding.chargeAccountGatewaysRv.adapter as PaymentGatewayGridAdapter).getSelectedItem()!!

        when (selectedgateway.id) {

            PaymentMethod.ACCOUNT_CHARGE.type -> {
                mLoadingDialog = LoadingDialog.instance
                mLoadingDialog.show(childFragmentManager, null)

                val paymentRequest = PaymentRequest()
                paymentRequest.orderId = mCheckout!!.mOrder.orderId
                mPresenter.requestPaymentFromCharge(paymentRequest)
            }

            else -> {
                mLoadingDialog = LoadingDialog.instance
                mLoadingDialog.show(childFragmentManager, null)

                val paymentRequest = PaymentRequest()
                paymentRequest.gatewayId = selectedgateway.id
                if (mCheckout == null) {
                    try {
                        paymentRequest.amount =
                            mBinding.chargeAccountPaymentAmountEdtx.text.toString().toDouble()
                    } catch (ex: NumberFormatException) {
                        ToastMaker.show(
                            requireContext(),
                            getString(R.string.error),
                            getString(R.string.enter_valid_number),
                            ToastMaker.Type.ERROR
                        )
                        return
                    }

                } else {
                    paymentRequest.orderId = mCheckout!!.mOrder.orderId
                }

                paymentRequest.orderId = mCheckout!!.mOrder.orderId

                val uri = Uri.parse(PAYMENT_GATEWAY_URL)
                    .buildUpon()
                    .appendQueryParameter("gateway_id", "4")
                    .appendQueryParameter("order_id", "502")
                    .build().toString()

                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                startActivity(browserIntent)

//                val postData="gateway_id=4&order_id=502"
//                mBinding.webView.postUrl(PAYMENT_GATEWAY_URL, postData.toByteArray())

//                mPresenter.requestPayment(paymentRequest)
            }
        }
    }

    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }

    override fun onItemSelected(paymentChannel: PaymentChannel, position: Int) {
        if (paymentChannel.id == PaymentMethod.OFFLINE_PAYMENT.type) {
            val amount = mBinding.chargeAccountPaymentAmountEdtx.text.toString()

            val bundle = Bundle()
            bundle.putString(App.AMOUNT, amount)

            val dialog = OfflinePaymentDialog()
            dialog.arguments = bundle
            dialog.setOnDismissListener(object : ItemCallback<Any> {
                override fun onItem(item: Any, vararg args: Any) {
                    try {
                        if (BuildVars.LOGS_ENABLED) {
                            Log.d("ChargeAccount", "onItemSelected -> setOnDismissListener")
                        }
                        val viewHolder =
                            mBinding.chargeAccountGatewaysRv.findViewHolderForAdapterPosition(
                                position
                            )
                                    as PaymentGatewayGridAdapter.ViewHolder
                        viewHolder.deselect(viewHolder.binding)

                        enableDisableBtn()
                    } catch (ex: Exception) {
                    }
                }
            })
            dialog.show(childFragmentManager, null)

            dialog.dialog?.setOnDismissListener {
                try {
                    if (BuildVars.LOGS_ENABLED) {
                        Log.d("ChargeAccount", "onItemSelected -> setOnDismissListener")
                    }
                    val viewHolder =
                        mBinding.chargeAccountGatewaysRv.findViewHolderForAdapterPosition(position)
                                as PaymentGatewayGridAdapter.ViewHolder
                    viewHolder.deselect(viewHolder.binding)

                    enableDisableBtn()
                } catch (ex: Exception) {
                }
            }

        } else if (mCheckout == null) {
            enableDisableBtn()
        } else {
            mBinding.chargeAccountPaymentPayBtn.isEnabled = true
        }
    }

    override fun onItemDeselected() {
        mBinding.chargeAccountPaymentPayBtn.isEnabled = false
    }

    fun onPaymentWithCharge(response: BaseResponse) {
        mLoadingDialog.dismiss()
        (activity as MainActivity).goToPaymentStatusPage(response.isSuccessful, mPaymentRedirection)
    }
}