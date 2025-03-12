package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.Subscription
import com.online.course.model.SubscriptionItem
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.SubscriptionFrag
import retrofit2.Call
import retrofit2.Response

class SubscriptionPresenterImpl(private val frag: SubscriptionFrag) :
    Presenter.SubscriptionPresenter {

    override fun getSubscriptions() {
        val subscriptionsReq = ApiService.apiClient!!.getSubscriptions()
        frag.addNetworkRequest(subscriptionsReq)
        subscriptionsReq.enqueue(object : CustomCallback<Data<Subscription>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getSubscriptions()
                }
            }

            override fun onResponse(
                call: Call<Data<Subscription>>,
                response: Response<Data<Subscription>>
            ) {
                if (response.body() != null) {
                    frag.onSubscriptionsReceived(response.body()!!.data!!)
                }
            }

        })
    }

    override fun checkoutSubscription(subscriptionItem: SubscriptionItem) {
        val checkoutSubscriptionReq = ApiService.apiClient!!.checkoutSubscription(subscriptionItem)
        frag.addNetworkRequest(checkoutSubscriptionReq)
        checkoutSubscriptionReq.enqueue(object :
            CustomCallback<Data<com.online.course.model.Response>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    checkoutSubscription(subscriptionItem)
                }
            }

            override fun onResponse(
                call: Call<Data<com.online.course.model.Response>>,
                response: Response<Data<com.online.course.model.Response>>
            ) {
                if (response.body() != null) {
                    frag.onCheckout(response.body()!!)
                }
            }

            override fun onFailure(
                call: Call<Data<com.online.course.model.Response>>,
                t: Throwable
            ) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        }

        )
    }
}