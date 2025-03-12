package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.Forums
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.ForumsFrag
import retrofit2.Call
import retrofit2.Response

class ForumsPresenterImpl(private val frag: ForumsFrag) : Presenter.ForumsPresenter {

    override fun getForumQuestions(courseId: Int) {
        val courseForum = ApiService.apiClient!!.getCourseForum(courseId)
        frag.addNetworkRequest(courseForum)
        courseForum.enqueue(object : CustomCallback<Data<Forums>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getForumQuestions(courseId)
                }
            }

            override fun onResponse(
                call: Call<Data<Forums>>,
                response: Response<Data<Forums>>
            ) {
                if (response.body() != null) {
                    frag.onForumReceived(response.body()!!.data!!)
                }
            }
        })
    }

    override fun searchInCourseForum(courseId: Int, s: String) {
        val searchInCourseForum = ApiService.apiClient!!.searchInCourseForum(courseId, s)
        frag.addNetworkRequest(searchInCourseForum)
        searchInCourseForum.enqueue(object : CustomCallback<Data<Forums>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getForumQuestions(courseId)
                }
            }

            override fun onResponse(
                call: Call<Data<Forums>>,
                response: Response<Data<Forums>>
            ) {
                if (response.body() != null) {
                    frag.onForumReceived(response.body()!!.data!!)
                }
            }
        })

    }
}