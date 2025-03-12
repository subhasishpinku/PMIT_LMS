package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.CategoryFrag
import retrofit2.Call
import retrofit2.Response

class CategoryPresenterImpl(private val frag: CategoryFrag) : Presenter.CategoryPresenter {

    override fun getCatFiltersAndCourses(categoryId: Int) {
        val catFiltersAndCourses = ApiService.apiClient!!.getCatFiltersAndCourses(categoryId)
        frag.addNetworkRequest(catFiltersAndCourses)
        catFiltersAndCourses.enqueue(object : CustomCallback<Data<CatCourses>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getCatFiltersAndCourses(categoryId)
                }
            }

            override fun onResponse(
                call: Call<Data<CatCourses>>,
                response: Response<Data<CatCourses>>
            ) {
                if (response.body() != null) {
                    frag.onResultReceived(response.body()!!)
                }
            }
        })
    }

    override fun getCatFeaturedCourses(categoryId: Int) {
        val featuredCourses = ApiService.apiClient!!.getFeaturedCourses(categoryId)
        frag.addNetworkRequest(featuredCourses)
        featuredCourses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getCatFeaturedCourses(categoryId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
                    frag.onFeaturedCoursesReceived(response.body()!!)
                }
            }
        })
    }

    override fun getCatFeaturedCourses(
        categoryId: Int,
        selectedOptions: ArrayList<KeyValuePair>?,
        selectedFilters: ArrayList<KeyValuePair>?
    ) {
        var url = "categories/${categoryId}/webinars?"

        if (selectedOptions != null) {
            for ((i, keyValuePair) in selectedOptions.withIndex()) {
                url += keyValuePair.key + "=" + keyValuePair.value
                if (i + 1 != selectedOptions.size) {
                    url += "&"
                }
            }
        }

        if (selectedFilters != null && selectedFilters.size > 0) {
            url += "&"
            for ((i, keyValue) in selectedFilters.withIndex()) {
                url += keyValue.key + "=" + keyValue.value
                if (i + 1 != selectedFilters.size) {
                    url += "&"
                }
            }
        }

        val catFilteredCourses = ApiService.apiClient!!.getCatFilteredCourses(url)
        frag.addNetworkRequest(catFilteredCourses)
        catFilteredCourses.enqueue(object : CustomCallback<Data<CatCourses>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getCatFiltersAndCourses(categoryId)
                }
            }

            override fun onResponse(
                call: Call<Data<CatCourses>>,
                response: Response<Data<CatCourses>>
            ) {
                if (response.body() != null) {
                    frag.onResultReceived(response.body()!!)
                }
            }
        })
    }

}