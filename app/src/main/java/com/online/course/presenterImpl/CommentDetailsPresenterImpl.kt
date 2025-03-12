package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.CommentDetailsFrag
import retrofit2.Call
import retrofit2.Response

class CommentDetailsPresenterImpl(private val frag: CommentDetailsFrag) :
    Presenter.CommentDetailsPresenter {

    override fun removeComment(commentId: Int) {
        ApiService.apiClient!!.removeComment(commentId)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        removeComment(commentId)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onCommentRemoved(response.body()!!)
                    }
                }
            })
    }
}