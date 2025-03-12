package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.OfflinePayment
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.FinancialOfflinePaymentsFrag
import retrofit2.Call
import retrofit2.Response

class FinancialOfflinePaymentsPresenterImpl(private val frag: FinancialOfflinePaymentsFrag) :
    Presenter.FinancialOfflinePaymentsPresenter {

    override fun getOfflinePayments() {
        val offlinePaymentsReq = ApiService.apiClient!!.getOfflinePayments()
        frag.addNetworkRequest(offlinePaymentsReq)
        offlinePaymentsReq.enqueue(object : CustomCallback<Data<List<OfflinePayment>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getOfflinePayments()
                }
            }

            override fun onResponse(
                call: Call<Data<List<OfflinePayment>>>,
                response: Response<Data<List<OfflinePayment>>>
            ) {
                if (response.body() != null) {
                    frag.onPaymentsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}