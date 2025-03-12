package com.online.course.presenterImpl

import com.google.gson.Gson
import com.online.course.manager.App
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.SignUpFrag
import retrofit2.Call
import retrofit2.Response

class SignUpPresenterImpl(private val frag: SignUpFrag) : ThirdPartyPresenterImpl(frag),
    Presenter.SignUpPresenter {

    override fun signUp(signUp: EmailSignUp) {
        val signUpRequest = ApiService.apiClient!!.signUpMethod(signUp)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(getCallback(emailSignUp = signUp))
    }

    override fun signUp(signUp: MobileSignUp) {
        val signUpRequest = ApiService.apiClient!!.signUpMethod(signUp)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(getCallback(mobileSignUp = signUp))
    }

    private fun getCallback(
        emailSignUp: EmailSignUp? = null,
        mobileSignUp: MobileSignUp? = null
    ): CustomCallback<Data<User>> {
        return object : CustomCallback<Data<User>> {

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    if (emailSignUp != null) {
                        signUp(emailSignUp)
                    } else {
                        signUp(mobileSignUp!!)
                    }
                }
            }

            override fun onResponse(call: Call<Data<User>>, response: Response<Data<User>>) {
                if (response.body() != null) {
                    if (emailSignUp != null) {
                        frag.onUserBasicsSaved(response.body()!!, emailSignUp = emailSignUp)
                    } else {
                        frag.onUserBasicsSaved(response.body()!!, mobileSignUp = mobileSignUp)
                    }

                } else {
                    val error = Gson().fromJson<BaseResponse>(
                        response.errorBody()?.string(),
                        BaseResponse::class.java
                    )

                    frag.onErrorOccured(error)
                }
            }
        }
    }
}