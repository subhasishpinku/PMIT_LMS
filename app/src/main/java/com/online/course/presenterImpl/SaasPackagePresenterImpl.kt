package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Data
import com.online.course.model.SaasPackage
import com.online.course.model.SaasPackageItem
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.SaasPackageFrag
import retrofit2.Call
import retrofit2.Response

class SaasPackagePresenterImpl(private val frag: SaasPackageFrag) : Presenter.SaasPackagePresenter {

    override fun getSaasPackages() {
        ApiService.apiClient!!.getSaasPackages()
            .enqueue(object : CustomCallback<Data<SaasPackage>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getSaasPackages()
                    }
                }

                override fun onResponse(
                    call: Call<Data<SaasPackage>>,
                    res: Response<Data<SaasPackage>>
                ) {
                    if (res.body() != null) {
                        frag.onSaasPackageReceived(res.body()!!.data!!)
                    }
                }

            })
    }

    override fun checkoutSubscription(saasPackageItem: SaasPackageItem) {
        ApiService.apiClient!!.checkoutSaasPackage(saasPackageItem)
            .enqueue(object : CustomCallback<Data<com.online.course.model.Response>> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        checkoutSubscription(saasPackageItem)
                    }
                }

                override fun onResponse(
                    call: Call<Data<com.online.course.model.Response>>,
                    res: Response<Data<com.online.course.model.Response>>
                ) {
                    if (res.body() != null) {
                        frag.onCheckout(res.body()!!)
                    }
                }

            })
    }
}