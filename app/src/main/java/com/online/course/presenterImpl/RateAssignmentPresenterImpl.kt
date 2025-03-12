package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Grade
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.RateAssignmentDialog
import retrofit2.Call
import retrofit2.Response

class RateAssignmentPresenterImpl(private val dialog: RateAssignmentDialog) :
    Presenter.RateAssignmentPresenter {

    override fun rateAssignment(assignmentId: Int, grade: Grade) {
        ApiService.apiClient!!.rateAssignment(assignmentId, grade)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        rateAssignment(assignmentId, grade)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onResponse(response.body()!!)
                    } else {
                        dialog.onRequestFailed()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    dialog.onRequestFailed()
                }
            })
    }
}