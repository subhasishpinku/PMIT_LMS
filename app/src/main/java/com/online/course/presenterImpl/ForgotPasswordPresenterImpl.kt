package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.ForgetPassword
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.ForgetPasswordFrag
import retrofit2.Call
import retrofit2.Response

class ForgotPasswordPresenterImpl(private val frag: ForgetPasswordFrag): Presenter.ForgotPasswordPresenter {

    override fun sendChangePasswordLink(forgetPassword: ForgetPassword) {
        val sendChangePasswordLinkReq = ApiService.apiClient!!.sendChangePasswordLink(forgetPassword)
        frag.addNetworkRequest(sendChangePasswordLinkReq)
        sendChangePasswordLinkReq.enqueue(object : CustomCallback<BaseResponse>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    sendChangePasswordLink(forgetPassword)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    frag.onPasswordChanged(response.body()!!)
                }
            }

        })
    }
}