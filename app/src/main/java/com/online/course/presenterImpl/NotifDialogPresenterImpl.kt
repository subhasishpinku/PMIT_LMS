package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.NotifDialog
import retrofit2.Call
import retrofit2.Response

class NotifDialogPresenterImpl(private val dialog: NotifDialog) : Presenter.NotifDialogPresenter {

    override fun setStatusToSeen(notifId: Int) {
        ApiService.apiClient!!.setNotifStatusToSeen(notifId)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        setStatusToSeen(notifId)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null && response.body()!!.isSuccessful) {
                        dialog.onNotifSatusChange()
                    }
                }
            })
    }
}