package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.CompletionCert
import com.online.course.model.Data
import com.online.course.model.Quiz
import com.online.course.model.QuizResult
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.CertificatesFrag
import retrofit2.Call
import retrofit2.Response

class CertificatesPresenterImpl(private val frag: CertificatesFrag) :
    Presenter.CertificatesPresenter {

    override fun getAchievementCertificates() {
        val achievementCertificatesReq = ApiService.apiClient!!.getAchievementCertificates()
        frag.addNetworkRequest(achievementCertificatesReq)
        achievementCertificatesReq
            .enqueue(object : CustomCallback<Data<List<QuizResult>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getAchievementCertificates()
                    }
                }

                override fun onResponse(
                    call: Call<Data<List<QuizResult>>>,
                    response: Response<Data<List<QuizResult>>>
                ) {
                    if (response.body() != null) {
                        frag.onCertsReceived(response.body()!!.data!!)
                    }
                }

            })
    }

    override fun getClassCertificates() {
        val classCertificates = ApiService.apiClient!!.getClassCertificates()
        frag.addNetworkRequest(classCertificates)
        classCertificates.enqueue(object : CustomCallback<Data<Data<List<Quiz>>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getAchievementCertificates()
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<Quiz>>>>,
                response: Response<Data<Data<List<Quiz>>>>
            ) {
                if (response.body() != null) {
                    frag.onClassCertsReceived(response.body()!!.data!!.data!!)
                }
            }

        })
    }

    override fun getCompletionCertificates() {
        val completionCertificates = ApiService.apiClient!!.getCompletionCertificates()
        frag.addNetworkRequest(completionCertificates)
        completionCertificates.enqueue(object : CustomCallback<Data<Data<List<CompletionCert>>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getAchievementCertificates()
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<CompletionCert>>>>,
                response: Response<Data<Data<List<CompletionCert>>>>
            ) {
                if (response.body() != null) {
                    frag.onCompletionCertsReceived(response.body()!!.data!!.data!!)
                }
            }

        })
    }

}