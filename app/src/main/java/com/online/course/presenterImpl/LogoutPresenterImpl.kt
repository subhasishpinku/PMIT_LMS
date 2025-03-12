package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.model.BaseResponse
import com.online.course.model.Follow
import com.online.course.presenter.Presenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutPresenterImpl: Presenter.LogoutPresenter {
    override fun logout() {
        ApiService.apiClient!!.logout(Follow()).enqueue(object : Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {

            }
        })
    }
}