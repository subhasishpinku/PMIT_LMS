package com.online.course.presenterImpl

import android.os.Looper
import android.util.Log
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.manager.Utils
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.QuizResult
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.CertificateDetailsFrag
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class CertificateDetailsPresenterImpl(private val frag: CertificateDetailsFrag) :
    Presenter.CertificateDetailsPresenter {

    override fun getStudents() {
        val certificateStudentsReq = ApiService.apiClient!!.getCertificateStudents()
        frag.addNetworkRequest(certificateStudentsReq)
        certificateStudentsReq.enqueue(object : CustomCallback<Data<List<QuizResult>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getStudents()
                }
            }

            override fun onResponse(
                call: Call<Data<List<QuizResult>>>,
                response: Response<Data<List<QuizResult>>>
            ) {
                if (response.body() != null) {
                    frag.onStudentsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}