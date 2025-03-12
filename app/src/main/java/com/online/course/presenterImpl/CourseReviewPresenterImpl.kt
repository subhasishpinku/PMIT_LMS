package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Review
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.CourseReviewDialog
import retrofit2.Call
import retrofit2.Response

class CourseReviewPresenterImpl(private val dialog: CourseReviewDialog) :
    Presenter.CourseReviewPresenter {

    override fun addReview(review: Review) {
        ApiService.apiClient!!.addCourseReview(review)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        addReview(review)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onReviewSaved(response.body()!!, review)
                    }
                }

            })
    }

}