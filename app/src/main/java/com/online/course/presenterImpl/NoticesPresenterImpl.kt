package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.Notice
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.NoticesFrag
import retrofit2.Call
import retrofit2.Response

class NoticesPresenterImpl(private val frag: NoticesFrag) : Presenter.NoticesPresenter {

    override fun getNotices(courseId: Int) {
        val notices = ApiService.apiClient!!.getNotices(courseId)
        frag.addNetworkRequest(notices)
        notices.enqueue(object : CustomCallback<Data<List<Notice>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getNotices(courseId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Notice>>>,
                response: Response<Data<List<Notice>>>
            ) {
                if (response.body() != null) {
                    frag.onNoticesReceived(response.body()!!.data!!)
                }
            }
        })
    }
}