package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.PaymentRequest
import com.online.course.model.Response
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.ChargeDialog
import retrofit2.Call

class ChargeAccountPresenterImpl(private val dialog: ChargeDialog) :
    Presenter.ChargeAccountPresenter {
    override fun chargeAccount(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.chargeAccount(paymentRequest)
            .enqueue(object : CustomCallback<Data<Response>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        chargeAccount(paymentRequest)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Response>>,
                    response: retrofit2.Response<Data<Response>>
                ) {
                    if (response.body() != null) {
                        dialog.onCheckout(response.body()!!)
                    }
                }

            })
    }

}