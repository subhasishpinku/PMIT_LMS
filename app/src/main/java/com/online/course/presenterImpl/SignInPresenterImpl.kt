package com.online.course.presenterImpl

import com.google.gson.Gson
import com.online.course.manager.net.ApiService
import com.online.course.model.BaseResponse
import com.online.course.model.Login
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.SignInFrag
import retrofit2.Call
import retrofit2.Response
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data

class SignInPresenterImpl(private val frag: SignInFrag) : ThirdPartyPresenterImpl(frag),
    Presenter.SignInPresenter {

    override fun login(login: Login) {
        val loginRequest = ApiService.apiClient!!.login(login)
        frag.addNetworkRequest(loginRequest)
        loginRequest.enqueue(object : CustomCallback<Data<com.online.course.model.Response>> {

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    login(login)
                }
            }

            override fun onResponse(
                call: Call<Data<com.online.course.model.Response>>,
                response: Response<Data<com.online.course.model.Response>>
            ) {
                if (response.body() != null) {
                    frag.onSuccessfulLogin(response.body()!!)
                } else {
                    val error = Gson().fromJson<BaseResponse>(
                        response.errorBody()?.string(),
                        BaseResponse::class.java
                    )

                    frag.onErrorOccured(error)
                }
            }

            override fun onFailure(
                call: Call<Data<com.online.course.model.Response>>,
                t: Throwable
            ) {
                super.onFailure(call, t)
                frag.onErrorOccured(null)
            }
        })
    }
}