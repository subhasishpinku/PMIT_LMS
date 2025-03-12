package com.online.course.presenterImpl

import com.online.course.manager.App
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.AppConfig
import com.online.course.presenter.Presenter
import com.online.course.ui.SplashScreenActivity
import retrofit2.Call
import retrofit2.Response

class SplashScreenPresenterImpl(private val activity: SplashScreenActivity) :
    Presenter.SplashScreenPresenter {

    override fun getAppConfig() {
        val customerSettings = ApiService.apiClient?.getAppConfig()
        activity.addNetworkRequest(customerSettings)
        customerSettings?.enqueue(object : CustomCallback<AppConfig> {
            override fun onResponse(call: Call<AppConfig>, response: Response<AppConfig>) {
                if (response.body() != null) {
                    App.appConfig = response.body()!!
                    activity.onAppConfigReceived()
                }
            }

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getAppConfig()
                }
            }

            override fun onFailure(call: Call<AppConfig>, t: Throwable) {
                activity.showNoNetFrag()
            }

        })
    }
}