package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.UserProfile
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.ProfileFrag
import retrofit2.Call
import retrofit2.Response

class ProfilePresenterImpl(private val frag: ProfileFrag) : Presenter.ProfilePresenter {

    override fun getUserProfile(userId: Int) {
        val userProfileReq = ApiService.apiClient!!.getUserProfile(userId)
        frag.addNetworkRequest(userProfileReq)
        userProfileReq.enqueue(object : CustomCallback<Data<Data<UserProfile>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getUserProfile(userId)
                }
            }

            override fun onResponse(
                call: Call<Data<Data<UserProfile>>>,
                response: Response<Data<Data<UserProfile>>>
            ) {
                if (response.body() != null) {
                    frag.onUserProfileReceived(response.body()!!.data!!)
                }
            }

        })
    }
}