package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Blog
import com.online.course.model.Category
import com.online.course.model.Count
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.BlogsFrag
import retrofit2.Call
import retrofit2.Response

class BlogPresenterImpl(private val frag: BlogsFrag) : Presenter.BlogPresenter {

    override fun getBlogs() {
        val blogs = ApiService.apiClient!!.getBlogs()
        frag.addNetworkRequest(blogs)
        blogs.enqueue(object : CustomCallback<Data<List<Blog>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBlogs()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Blog>>>,
                response: Response<Data<List<Blog>>>
            ) {
                if (response.body() != null) {
                    frag.onBlogsRecevied(response.body()!!.data!!)
                }
            }
        })
    }

    override fun getBlogs(catId: Int) {
        val blogs = ApiService.apiClient!!.getBlogs(catId)
        frag.addNetworkRequest(blogs)
        blogs.enqueue(object : CustomCallback<Data<List<Blog>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBlogs(catId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Blog>>>,
                response: Response<Data<List<Blog>>>
            ) {
                if (response.body() != null) {
                    frag.onBlogsRecevied(response.body()!!.data!!)
                }
            }
        })
    }
}