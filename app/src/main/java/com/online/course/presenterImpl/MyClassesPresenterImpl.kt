package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Count
import com.online.course.model.Course
import com.online.course.model.Data
import com.online.course.model.MyClasses
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.MyClassesFrag
import com.online.course.ui.frag.MyClassesTabFrag
import retrofit2.Call
import retrofit2.Response

class MyClassesPresenterImpl(private val frag: MyClassesFrag) : Presenter.MyClassesPresenter {

    override fun getMyClasses() {
        val myClassesPageDataReq = ApiService.apiClient!!.getMyClassesPageData()
        frag.addNetworkRequest(myClassesPageDataReq)
        myClassesPageDataReq.enqueue(object : CustomCallback<MyClasses> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getMyClasses()
                }
            }

            override fun onResponse(call: Call<MyClasses>, response: Response<MyClasses>) {
                if (response.body() != null) {
                    frag.onMyClassesReceived(response.body()!!)
                }
            }

        })
    }

    override fun getPurchased() {
        val myPurchasesReq = ApiService.apiClient!!.getMyPurchases()
        frag.addNetworkRequest(myPurchasesReq)
        myPurchasesReq.enqueue(object : CustomCallback<Data<Count<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getPurchased()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Course>>>,
                response: Response<Data<Count<Course>>>
            ) {

                if (response.body() != null) {
                    frag.onPurchasedReceived(response.body()!!.data!!.items, true)
                }
            }

        })
    }

    override fun getOrganizations() {
        val myPurchasesReq = ApiService.apiClient!!.getCoursesOfOrganizations()
        frag.addNetworkRequest(myPurchasesReq)
        myPurchasesReq.enqueue(object : CustomCallback<Data<Count<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getPurchased()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Course>>>,
                response: Response<Data<Count<Course>>>
            ) {

                if (response.body() != null) {
                    frag.onPurchasedReceived(response.body()!!.data!!.items, false)
                }
            }

        })
    }

}