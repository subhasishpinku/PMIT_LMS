package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.QuizResult
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.QuizResultInfoFrag
import retrofit2.Call
import retrofit2.Response

class QuizResultInfoPresenterImpl(private val frag: QuizResultInfoFrag) :
    Presenter.QuizResultInfoPresenter {

    override fun startQuiz(id: Int) {
        val startQuizReq = ApiService.apiClient!!.startQuiz(id)
        frag.addNetworkRequest(startQuizReq)
        startQuizReq.enqueue(object : CustomCallback<Data<QuizResult>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    startQuiz(id)
                }
            }

            override fun onResponse(
                call: Call<Data<QuizResult>>,
                response: Response<Data<QuizResult>>
            ) {
                if (response.body() != null && response.body()!!.isSuccessful) {
                    frag.onQuizStartBegin(response.body()!!)
                } else {
                    frag.cannotStartQuiz(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Data<QuizResult>>, t: Throwable) {
                super.onFailure(call, t)
                frag.cannotStartQuiz(null)
            }

        })
    }

    override fun getQuizResult(quizId: Int) {
        val quizResultReq = ApiService.apiClient!!.getQuizResult(quizId)
        frag.addNetworkRequest(quizResultReq)
        quizResultReq.enqueue(object : CustomCallback<Data<QuizResult>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getQuizResult(quizId)
                }
            }

            override fun onResponse(
                call: Call<Data<QuizResult>>,
                response: Response<Data<QuizResult>>
            ) {
                if (response.body() != null) {
                    frag.initStudentResult(response.body()!!.data!!)
                }
            }

            override fun onFailure(call: Call<Data<QuizResult>>, t: Throwable) {
                super.onFailure(call, t)
                frag.cannotStartQuiz(null)
            }

        })

    }
}