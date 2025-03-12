package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Course
import com.online.course.model.Data
import com.online.course.model.Follow
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.PricingPlansDialog
import retrofit2.Call
import retrofit2.Response

class PricingPlansPresenterImpl(private val dialog: PricingPlansDialog) :
    Presenter.PricingPlansPresenter {

    override fun purchaseWithPoints(course: Course) {
        val retryListener = RetryListener { purchaseWithPoints(course) }
        val callback = getCallback(retryListener)
        if (course.isBundle()) {
            ApiService.apiClient!!.bundlePurchaseWithPoints(course.id, Follow()).enqueue(callback)
        } else {
            ApiService.apiClient!!.purchaseWithPoints(course.id, Follow()).enqueue(callback)
        }
    }

    private fun getCallback(retryListener: RetryListener): CustomCallback<BaseResponse> {
        return object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener {
                return retryListener
            }

            override fun onResponse(
                call: Call<BaseResponse>,
                res: Response<BaseResponse>
            ) {
                if (res.body() != null) {
                    dialog.onPurchase(res.body()!!)
                }
            }
        }
    }
}