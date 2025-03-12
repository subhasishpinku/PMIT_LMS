package com.online.course.presenterImpl

import com.google.gson.Gson
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Data
import com.online.course.model.User
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.UserRegistrationDialog
import retrofit2.Call
import retrofit2.Response

class UserRegistrationPresenterImpl(private val dialog: UserRegistrationDialog) :
    Presenter.UserRegistrationPresenter {

    override fun register(user: User) {
        val registerUserRequest = ApiService.apiClient!!.registerUser(user)
        dialog.addNetworkRequest(registerUserRequest)
        registerUserRequest.enqueue(object : CustomCallback<Data<com.online.course.model.Response>> {

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    register(user)
                }
            }

            override fun onResponse(call: Call<Data<com.online.course.model.Response>>,
                                    response: Response<Data<com.online.course.model.Response>>) {
                if (response.body() != null) {
                    dialog.onRegistrationSaved(response.body()!!, user)

                } else {
                    val error = Gson().fromJson<BaseResponse>(
                        response.errorBody()?.string(),
                        BaseResponse::class.java
                    )

                    dialog.onErrorOccured(error)
                }
            }
        })
    }
}