package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.ChangePassword
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.SettingsSecurityFrag
import retrofit2.Call
import retrofit2.Response

class SettingsSecurityPresenterImpl(private val frag: SettingsSecurityFrag) : Presenter.SettingsSecurityPresenter {

    override fun changePassword(changePassword: ChangePassword) {
        ApiService.apiClient!!.changePassword(changePassword)
            .enqueue(object : CustomCallback<Data<com.online.course.model.Response>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        changePassword(changePassword)
                    }
                }

                override fun onResponse(
                    call: Call<Data<com.online.course.model.Response>>,
                    response: Response<Data<com.online.course.model.Response>>
                ) {
                    if (response.body() != null) {
                        frag.onPasswordChanges(response.body()!!)
                    }
                }

            })
    }

}