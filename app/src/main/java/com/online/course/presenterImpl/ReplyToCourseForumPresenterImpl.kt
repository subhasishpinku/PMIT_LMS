package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.ForumItem
import com.online.course.model.Reply
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.ForumReplyDialog
import retrofit2.Call
import retrofit2.Response

class ReplyToCourseForumPresenterImpl(val dialog: ForumReplyDialog) :
    Presenter.ReplyToCourseForumPresenter {

    override fun reply(forum: ForumItem, reply: Reply) {
        if (forum.isAnswer()) {
            editReply(forum.id, reply)
        } else {
            replyToQuestion(forum.id, reply)
        }
    }

    private fun editReply(id: Int, reply: Reply) {
        ApiService.apiClient!!.editReplyToForumQuestion(id, reply)
            .enqueue(getCallback { editReply(id, reply) })
    }

    private fun replyToQuestion(id: Int, reply: Reply) {
        ApiService.apiClient!!.replyToForumQuestion(id, reply)
            .enqueue(getCallback { replyToQuestion(id, reply) })
    }

    fun getCallback(retryListener: RetryListener): CustomCallback<BaseResponse> {
        return object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener {
                return retryListener
            }

            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.body() != null) {
                    dialog.onRsp(response.body()!!)
                }
            }
        }
    }

}