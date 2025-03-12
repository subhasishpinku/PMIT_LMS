package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.QuizAnswer
import com.online.course.model.QuizResult
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.QuizFrag
import retrofit2.Call
import retrofit2.Response

class QuizPresenterImpl(private val frag: QuizFrag) : Presenter.QuizPresenter {

    override fun storeResult(quizId: Int, answer: QuizAnswer) {
        ApiService.apiClient!!.storeQuizResult(quizId, answer)
            .enqueue(object : CustomCallback<Data<Data<QuizResult>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        storeResult(quizId, answer)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Data<QuizResult>>>,
                    response: Response<Data<Data<QuizResult>>>
                ) {
                    if (response.body() != null) {
                        frag.onQuizResultSaved(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<Data<Data<QuizResult>>>, t: Throwable) {
                    super.onFailure(call, t)
                    frag.onRequestFailed()
                }
            })
    }
}