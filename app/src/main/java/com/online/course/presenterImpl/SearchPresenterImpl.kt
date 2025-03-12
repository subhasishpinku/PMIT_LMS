package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Count
import com.online.course.model.Course
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.HomeFrag
import com.online.course.ui.frag.SearchFrag
import retrofit2.Call
import retrofit2.Response

class SearchPresenterImpl(private val frag: SearchFrag) : Presenter.SearchPresenter {

    override fun getBestRatedCourses() {
        val map = HashMap<String, String>()
        map["offset"] = "0"
        map["limit"] = "3"
        map["sort"] = "best_rates"

        val courses = ApiService.apiClient!!.getCourses(map)
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBestRatedCourses()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
                    frag.onBestRatedCoursesRecevied(response.body()!!)
                }
            }
        })
    }
}