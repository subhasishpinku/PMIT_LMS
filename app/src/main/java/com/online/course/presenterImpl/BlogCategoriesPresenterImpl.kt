package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Category
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.BlogCategoriesDialog
import retrofit2.Call
import retrofit2.Response

class BlogCategoriesPresenterImpl(private val dialog: BlogCategoriesDialog) : Presenter.BlogCategoriesPresenter {
    override fun getBlogCategories() {
        ApiService.apiClient!!.getBlogCategories()
            .enqueue(object : CustomCallback<Data<List<Category>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getBlogCategories()
                    }
                }

                override fun onResponse(
                    call: Call<Data<List<Category>>>,
                    response: Response<Data<List<Category>>>
                ) {
                    if (response.body() != null) {
                        dialog.onBlogCatsReceived(response.body()!!.data!!)
                    }
                }

            })
    }
}