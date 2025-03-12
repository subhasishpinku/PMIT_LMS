package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Assignment
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.AssignmentOverviewFrag
import retrofit2.Call
import retrofit2.Response

class AssignmentOverviewPresenterImpl(val frag: AssignmentOverviewFrag) :
    Presenter.AssignmentOverviewPresenter {

    override fun getAssignmentStudents(assignmentId: Int) {
        val assignmentStudents = ApiService.apiClient!!.getAssignmentStudents(assignmentId)
        frag.addNetworkRequest(assignmentStudents)
        assignmentStudents.enqueue(object : CustomCallback<Data<List<Assignment>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getAssignmentStudents(assignmentId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Assignment>>>,
                response: Response<Data<List<Assignment>>>
            ) {
                if (response.body() != null) {
                    frag.onStudentsReceived(response.body()!!.data!!)
                }
            }
        })
    }

    override fun getAssignment(assignmentId: Int) {
        val assignment = ApiService.apiClient!!.getAssignment(assignmentId)
        frag.addNetworkRequest(assignment)
        assignment.enqueue(object : CustomCallback<Data<Assignment>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getAssignmentStudents(assignmentId)
                }
            }

            override fun onResponse(
                call: Call<Data<Assignment>>,
                response: Response<Data<Assignment>>
            ) {
                if (response.body() != null) {
                    frag.onAssignmentReceived(response.body()!!.data!!)
                }
            }
        })
    }

}