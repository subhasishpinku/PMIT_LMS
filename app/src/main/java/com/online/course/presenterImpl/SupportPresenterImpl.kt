package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.Ticket
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.SupportFrag
import retrofit2.Call
import retrofit2.Response

class SupportPresenterImpl(private val frag: SupportFrag) : Presenter.SupportPresenter {

    override fun getTickets() {
        val supportsReq = ApiService.apiClient!!.getTickets()
        frag.addNetworkRequest(supportsReq)
        supportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getTickets()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }

    override fun getClassSupport() {
        val classSupportsReq = ApiService.apiClient!!.getClassSupports()
        frag.addNetworkRequest(classSupportsReq)
        classSupportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getClassSupport()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }

    override fun getMyClassSupport() {
        val myClassSupportsReq = ApiService.apiClient!!.getMyClassSupports()
        frag.addNetworkRequest(myClassSupportsReq)
        myClassSupportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getMyClassSupport()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }
}