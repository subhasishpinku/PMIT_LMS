package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Message
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.NewMessageDialog
import retrofit2.Call
import retrofit2.Response

class NewMessagePresenterImpl(private val dialog: NewMessageDialog) :
    Presenter.NewMessagePresenter {


    override fun addMessage(userId: Int, message: Message) {
        ApiService.apiClient!!.addNewMessage(userId, message)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        addMessage(userId, message)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMessageAdded(response.body()!!)
                    }
                }

            })
    }
}