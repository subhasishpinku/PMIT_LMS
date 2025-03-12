package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.PayoutRequestDialog
import retrofit2.Call
import retrofit2.Response

class PayoutRequestPresenterImpl(private val dialog: PayoutRequestDialog) :
    Presenter.PayoutRequestPresenter {

    override fun requestPayout() {
        ApiService.apiClient!!.requestPayout(Any()).enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    requestPayout()
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    dialog.onPayoutSaved(response.body()!!)
                }
            }

        })
    }
}