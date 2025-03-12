package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.AddToCart
import com.online.course.model.BaseResponse
import com.online.course.model.Course
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.CourseDetailsFrag
import retrofit2.Call
import retrofit2.Response

class CourseDetailsPresenterImpl(private val frag: CourseDetailsFrag) :
    Presenter.CourseDetailsPresenter {

    override fun subscribe(addToCart: AddToCart) {
        ApiService.apiClient!!.subscribe(addToCart).enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    subscribe(addToCart)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    frag.onSubscribed(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }

    override fun addCourseToUserCourse(courseId: Int) {
        val addFreeCourseReq = ApiService.apiClient!!.addFreeCourse(courseId)
        frag.addNetworkRequest(addFreeCourseReq)
        addFreeCourseReq.enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    addCourseToUserCourse(courseId)
                }
            }

            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.body() != null) {
                    frag.onCourseAdded(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }
}