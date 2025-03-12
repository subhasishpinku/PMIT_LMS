package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.QuizAnswerItem
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.QuizReviewFrag
import retrofit2.Call
import retrofit2.Response

class QuizReviewPresenterImpl(private val frag: QuizReviewFrag) : Presenter.QuizReviewPresenter {

    override fun storeReviewResult(resultId: Int, review: List<QuizAnswerItem>) {
        ApiService.apiClient!!.storeReviewResult(resultId, review)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        storeReviewResult(resultId, review)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onResultStored(response.body()!!)
                    }
                }

            })
    }
}