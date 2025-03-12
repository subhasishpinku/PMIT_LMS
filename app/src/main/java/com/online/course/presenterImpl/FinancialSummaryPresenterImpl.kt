package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Count
import com.online.course.model.Data
import com.online.course.model.FinancialSummary
import com.online.course.model.PaymentRequest
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.FinancialSummaryFrag
import retrofit2.Call
import retrofit2.Response

class FinancialSummaryPresenterImpl(private val frag: FinancialSummaryFrag) :
    Presenter.FinancialSummaryPresenter {

    override fun getSummary() {
        val financialSummaryReq = ApiService.apiClient!!.getFinancialSummary()
        frag.addNetworkRequest(financialSummaryReq)
        financialSummaryReq.enqueue(object : CustomCallback<Data<Count<FinancialSummary>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getSummary()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<FinancialSummary>>>,
                response: Response<Data<Count<FinancialSummary>>>
            ) {
                if (response.body() != null) {
                    frag.onSummariesReceived(response.body()!!.data!!.items)
                }
            }
        })
    }

    override fun chargeAccount(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.chargeAccount(paymentRequest)
            .enqueue(object : CustomCallback<Data<com.online.course.model.Response>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        chargeAccount(paymentRequest)
                    }
                }

                override fun onResponse(
                    call: Call<Data<com.online.course.model.Response>>,
                    response: retrofit2.Response<Data<com.online.course.model.Response>>
                ) {
                    if (response.body() != null) {
                        frag.onCheckout(response.body()!!)
                    }
                }

            })
    }

}