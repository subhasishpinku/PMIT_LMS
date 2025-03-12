package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.UserChangeSettings
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.SettingsGeneralFrag
import retrofit2.Call
import retrofit2.Response

class SettingsGeneralPresenterImpl(private val frag: SettingsGeneralFrag) :
    Presenter.SettingsGeneralPresenter {

    override fun changeProfileSettings(changeSettings: UserChangeSettings) {
        ApiService.apiClient!!.changeProfileSettings(changeSettings)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        changeProfileSettings(changeSettings)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onSettingsChanged(response.body()!!, changeSettings)
                    }
                }

            })
    }
}