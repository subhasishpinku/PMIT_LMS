package com.online.course.presenterImpl

import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Comments
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.CommentsFrag
import retrofit2.Call
import retrofit2.Response

class CommentsPresenterImpl() : Presenter.CommentsPresenter {

    override fun getComments(callback: ItemCallback<Comments>) {
        ApiService.apiClient!!.getComments().enqueue(object : CustomCallback<Data<Comments>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getComments(callback)
                }
            }

            override fun onResponse(
                call: Call<Data<Comments>>,
                response: Response<Data<Comments>>
            ) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!.data!!)
                }
            }
        })
    }
}