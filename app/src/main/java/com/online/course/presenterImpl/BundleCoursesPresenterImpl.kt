package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Course
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.BundleCoursesFrag
import retrofit2.Call
import retrofit2.Response

class BundleCoursesPresenterImpl(private val frag: BundleCoursesFrag) :
    Presenter.BundleCoursesPresenter {

    override fun getBundleCourses(id: Int) {
        val coursesForBundle = ApiService.apiClient!!.getCoursesForBundle(id)
        frag.addNetworkRequest(coursesForBundle)
        coursesForBundle.enqueue(object : CustomCallback<Data<Data<List<Course>>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getBundleCourses(id)
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<Course>>>>,
                response: Response<Data<Data<List<Course>>>>
            ) {
                if (response.body() != null) {
                    frag.onCoursesReceived(response.body()!!.data!!.data!!)
                }
            }
        })
    }
}