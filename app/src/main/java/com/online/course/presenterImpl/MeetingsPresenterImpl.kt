package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.Meetings
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.MeetingsTabFrag
import retrofit2.Call
import retrofit2.Response

class MeetingsPresenterImpl(private val frag: MeetingsTabFrag) : Presenter.MeetingsPresenter {

    override fun getMeetings() {
        val meetingsReq = ApiService.apiClient!!.getMeetings()
        frag.addNetworkRequest(meetingsReq)
        meetingsReq.enqueue(object : CustomCallback<Data<Meetings>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getMeetings()
                }
            }

            override fun onResponse(
                call: Call<Data<Meetings>>,
                response: Response<Data<Meetings>>
            ) {
                if (response.body() != null) {
                    frag.onMeetingsReceived(response.body()!!.data!!)
                }
            }
        })
    }
}