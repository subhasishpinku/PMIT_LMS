package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.ForumItemAnswer
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.ForumAnswersFrag
import retrofit2.Call
import retrofit2.Response

class ForumAnswersPresenterImpl(val frag: ForumAnswersFrag) : Presenter.ForumAnswersPresenter {

    override fun getForumQuestionAnswers(forumId: Int) {
        val forumQuestionAnswers = ApiService.apiClient!!.getForumQuestionAnswers(forumId)
        frag.addNetworkRequest(forumQuestionAnswers)
        forumQuestionAnswers.enqueue(object : CustomCallback<Data<Data<List<ForumItemAnswer>>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getForumQuestionAnswers(forumId)
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<ForumItemAnswer>>>>,
                response: Response<Data<Data<List<ForumItemAnswer>>>>
            ) {
                if (response.body() != null) {
                    frag.onAnswersReceived(response.body()!!.data!!.data!!)
                }
            }
        })
    }
}