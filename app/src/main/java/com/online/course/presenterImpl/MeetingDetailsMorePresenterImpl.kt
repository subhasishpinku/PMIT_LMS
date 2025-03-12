package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.MeetingDetailsMoreDialog
import retrofit2.Call
import retrofit2.Response

class MeetingDetailsMorePresenterImpl(private val dialog: MeetingDetailsMoreDialog) :
    Presenter.MeetingDetailsMorePresenter {

    override fun finishMeeting(meetingId: Int) {
        ApiService.apiClient!!.finishMeeting(meetingId, Any())
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        finishMeeting(meetingId)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMeetingFinished(response.body()!!)
                    }
                }
            })
    }
}