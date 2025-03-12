package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Conversation
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.AssignmentConversationDialog
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class AssignmentNewConversationPresenterImpl(private val dialog: AssignmentConversationDialog) :
    Presenter.AssignmentNewConversationPresenter {

    override fun saveConversation(assignmentId: Int, conversation: Conversation, file: File?) {
        val fileBody = file?.asRequestBody()
        val filePart = fileBody?.let {
            MultipartBody.Part.createFormData("file_path", file.name,
                it
            )
        }

        var studentPart: MultipartBody.Part? = null
        if (conversation.studentId > 0) {
            studentPart = MultipartBody.Part.createFormData(
                "student_id",
                conversation.studentId.toString()
            )
        }

        ApiService.apiClient!!.saveAssignmentConversation(
            assignmentId,
            MultipartBody.Part.createFormData("file_title", conversation.fileTitle),
            MultipartBody.Part.createFormData("message", conversation.message),
            filePart,
            studentPart
        ).enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    saveConversation(assignmentId, conversation, file)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    dialog.onResponse(response.body()!!)
                }
            }
        })
    }
}