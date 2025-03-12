package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.SalesRes
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.FinancialSalesFrag
import retrofit2.Call
import retrofit2.Response

class FinancialSalesPresenterImpl(private val frag: FinancialSalesFrag) :
    Presenter.FinancialSalesPresenter {

    override fun getSales() {
        val salesReq = ApiService.apiClient!!.getSales()
        frag.addNetworkRequest(salesReq)
        salesReq.enqueue(object : CustomCallback<Data<SalesRes>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getSales()
                }
            }

            override fun onResponse(
                call: Call<Data<SalesRes>>,
                response: Response<Data<SalesRes>>
            ) {
                if (response.body() != null) {
                    frag.onSalesReceived(response.body()!!.data!!)
                }
            }

        })
    }
}