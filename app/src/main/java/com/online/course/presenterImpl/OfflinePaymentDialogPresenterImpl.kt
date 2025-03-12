package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.OfflinePayment
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.OfflinePaymentDialog
import retrofit2.Call
import retrofit2.Response

class OfflinePaymentDialogPresenterImpl(private val dialog: OfflinePaymentDialog) :
    Presenter.OfflinePaymentDialogPresenter {

    override fun addOfflinePayment(offlinePayment: OfflinePayment) {
        val addOfflinePaymentsReq = ApiService.apiClient!!.addOfflinePayments(offlinePayment)
        dialog.addNetworkRequest(addOfflinePaymentsReq)
        addOfflinePaymentsReq.enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    addOfflinePayment(offlinePayment)
                }
            }

            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.body() != null) {
                    dialog.onOfflinePaymentSaved(response.body()!!)
                }
            }

        })
    }
}