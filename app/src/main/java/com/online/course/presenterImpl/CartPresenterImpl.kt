package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.CartFrag
import retrofit2.Call
import retrofit2.Response

class CartPresenterImpl(private val frag: CartFrag) : Presenter.CartPresenter {

    override fun getCart() {
        val cartReq = ApiService.apiClient!!.getCart()
        frag.addNetworkRequest(cartReq)
        cartReq.enqueue(object : CustomCallback<Data<Data<Cart?>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getCart()
                }
            }

            override fun onResponse(
                call: Call<Data<Data<Cart?>>>,
                response: Response<Data<Data<Cart?>>>
            ) {
                if (response.body() != null) {
                    frag.onCartReceived(response.body()!!.data!!.data)
                }
            }

            override fun onFailure(call: Call<Data<Data<Cart?>>>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }

        })
    }

    override fun removeFromCart(cartItemId: Int, position: Int) {
        ApiService.apiClient!!.removeFromCart(cartItemId)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        removeFromCart(cartItemId, position)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onCartRemoved(response.body()!!, position)
                    }
                }

            })
    }

    override fun checkout(coupon: Coupon?) {
        val cp = coupon ?: Coupon()
        val checkoutReq = ApiService.apiClient!!.checkout(cp)
        frag.addNetworkRequest(checkoutReq)
        checkoutReq.enqueue(object : CustomCallback<Data<com.online.course.model.Response>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    checkout(coupon)
                }
            }

            override fun onResponse(call: Call<Data<com.online.course.model.Response>>, response: Response<Data<com.online.course.model.Response>>) {
                if (response.body() != null) {
                    frag.onCheckout(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Data<com.online.course.model.Response>>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }

        })
    }
}