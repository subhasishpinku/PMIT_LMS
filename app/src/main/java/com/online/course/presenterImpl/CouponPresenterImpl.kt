package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Coupon
import com.online.course.model.CouponValidation
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.CouponDialog
import retrofit2.Call
import retrofit2.Response

class CouponPresenterImpl(private val dialog: CouponDialog) : Presenter.CouponPresenter {

    override fun validateCoupon(coupon: Coupon) {
        ApiService.apiClient!!.validateCoupon(coupon)
            .enqueue(object : CustomCallback<Data<CouponValidation>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        validateCoupon(coupon)
                    }
                }

                override fun onResponse(
                    call: Call<Data<CouponValidation>>,
                    response: Response<Data<CouponValidation>>
                ) {
                    if (response.body() != null) {
                        dialog.onCouponValidated(response.body()!!)
                    }
                }

            })
    }


}