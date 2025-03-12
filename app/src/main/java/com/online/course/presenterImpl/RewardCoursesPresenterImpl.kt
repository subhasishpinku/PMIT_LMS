package com.online.course.presenterImpl

import com.online.course.manager.Utils.toInt
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Course
import com.online.course.model.Data
import com.online.course.model.KeyValuePair
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.RewardCoursesFrag
import retrofit2.Call
import retrofit2.Response

class RewardCoursesPresenterImpl(private val frag: RewardCoursesFrag) :
    Presenter.RewardCoursesPresenter {

    override fun getRewardCourses(categories: List<KeyValuePair>?, options: List<KeyValuePair>?) {
        val filter = HashMap<String, String>()
        filter["reward"] = true.toInt().toString()

        if (!categories.isNullOrEmpty()){
            filter[categories[0].key] = categories[0].value
        }

        if (!options.isNullOrEmpty()) {
            for (option in options) {
                filter[option.key] = option.value
            }
        }

        ApiService.apiClient!!.getCourses(filter).enqueue(object : CustomCallback<Data<List<Course>>>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getRewardCourses(categories, options)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                res: Response<Data<List<Course>>>
            ) {
                if (res.body() != null) {
                    frag.onResultReceived(res.body()!!.data!!)
                }
            }

        })
    }
}