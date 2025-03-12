package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Assignment
import com.online.course.model.Conversation
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.AssignmentConversationFrag
import retrofit2.Call
import retrofit2.Response

class AssignmentConversationPresenterImpl(private val frag: AssignmentConversationFrag) :
    Presenter.AssignmentConversationPresenter {

    override fun getConversations(assignment: Assignment, studentId: Int?) {
        val assignmentConversations =
            ApiService.apiClient!!.getAssignmentConversations(assignment.id, studentId)
        frag.addNetworkRequest(assignmentConversations)
        assignmentConversations.enqueue(object : CustomCallback<Data<List<Conversation>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getConversations(assignment, studentId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Conversation>>>,
                response: Response<Data<List<Conversation>>>
            ) {
                if (response.body() != null) {
                    frag.onConversationsReceived(response.body()!!.data!!)
                }
            }
        })
    }

}