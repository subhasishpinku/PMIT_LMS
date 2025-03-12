package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.PayoutRes
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.FinancialPayoutFrag
import retrofit2.Call
import retrofit2.Response

class FinancialPayoutPresenterImpl(private val frag: FinancialPayoutFrag) :
    Presenter.FinancialPayoutPresenter {

    override fun getPayouts() {
        val payoutsReq = ApiService.apiClient!!.getPayouts()
        frag.addNetworkRequest(payoutsReq)
        payoutsReq.enqueue(object :CustomCallback<Data<PayoutRes>>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getPayouts()
                }
            }

            override fun onResponse(
                call: Call<Data<PayoutRes>>,
                response: Response<Data<PayoutRes>>
            ) {
                if (response.body() != null) {
                    frag.onPayoutsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}