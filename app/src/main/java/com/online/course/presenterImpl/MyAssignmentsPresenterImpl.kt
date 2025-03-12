package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Assignment
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.MyAssignmentsFrag
import retrofit2.Call
import retrofit2.Response

class MyAssignmentsPresenterImpl(private val frag: MyAssignmentsFrag) :
    Presenter.MyAssignmentsPresenter {

    override fun getMyAssignments() {
        val myAssignments = ApiService.apiClient!!.getMyAssignments()
        frag.addNetworkRequest(myAssignments)
        myAssignments.enqueue(object : CustomCallback<Data<Data<List<Assignment>>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getMyAssignments()
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<Assignment>>>>,
                response: Response<Data<Data<List<Assignment>>>>
            ) {
                if (response.body() != null) {
                    frag.onAssignmentsReceived(response.body()!!.data!!.data!!)
                }
            }

        })
    }

}