package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.ReserveMeeting
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.MeetingJoinDetailsDialog
import retrofit2.Call
import retrofit2.Response

class MeetingJoinDetailsPresenterImpl(private val dialog: MeetingJoinDetailsDialog) :
    Presenter.MeetingJoinDetailsPresenter {

    override fun createJoin(reserveMeeting: ReserveMeeting) {
        ApiService.apiClient!!.createJoinForMeeting(reserveMeeting)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        createJoin(reserveMeeting)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMeetingJoinAdded(response.body()!!)
                    }
                }

            })
    }
}