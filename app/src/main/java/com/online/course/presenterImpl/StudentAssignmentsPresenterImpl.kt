package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.StudentAssignments
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.StudentAssignmentsFrag
import retrofit2.Call
import retrofit2.Response

class StudentAssignmentsPresenterImpl(private val frag: StudentAssignmentsFrag) :
    Presenter.StudentAssignmentsPresenter {

    override fun getStudentAssignments() {
        val studentAssignments = ApiService.apiClient!!.getStudentAssignments()
        frag.addNetworkRequest(studentAssignments)
        studentAssignments.enqueue(object : CustomCallback<Data<StudentAssignments>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getStudentAssignments()
                }
            }

            override fun onResponse(
                call: Call<Data<StudentAssignments>>,
                response: Response<Data<StudentAssignments>>
            ) {
                if (response.body() != null) {
                    frag.onAssignmentsReceived(response.body()!!.data!!)
                }
            }
        })
    }
}