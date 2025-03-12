package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.TicketConversationFrag
import retrofit2.Call
import retrofit2.Response

class TicketConversationPresenterImpl(private val frag: TicketConversationFrag) :
    Presenter.TicketConversationPresenter {

    override fun closeTicket(ticketId: Int) {
        ApiService.apiClient!!.closeTicket(ticketId).enqueue(object :CustomCallback<BaseResponse>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    closeTicket(ticketId)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    frag.onTicketClosed(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }
}