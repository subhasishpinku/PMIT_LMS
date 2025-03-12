package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Follow
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.ProfileAboutFrag
import retrofit2.Call
import retrofit2.Response

class ProfileAboutPresenterImpl(private val frag: ProfileAboutFrag) :
    Presenter.ProfileAboutPresenter {

    override fun follow(userId: Int, follow: Follow) {
        ApiService.apiClient!!.followUser(userId, follow)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        follow(userId, follow)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onFollowed(follow)
                    }
                }
            })
    }
}