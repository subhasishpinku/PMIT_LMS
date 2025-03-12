package com.online.course.presenterImpl

import android.util.Log
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Count
import com.online.course.model.Data
import com.online.course.model.Notif
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.NotifsFrag
import retrofit2.Call
import retrofit2.Response

class NotifPresenterImpl(private val frag: NotifsFrag) : Presenter.NotifPresenter {

    override fun getNotifs() {
        val notifsReq = ApiService.apiClient!!.getNotifs()
        frag.addNetworkRequest(notifsReq)
        notifsReq.enqueue(object :CustomCallback<Data<Count<Notif>>>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getNotifs()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Notif>>>,
                response: Response<Data<Count<Notif>>>
            ) {
                if (response.body() != null) {
                    frag.onNotifsReceived(response.body()!!)
                }
            }
        })
    }
}