package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Category
import com.online.course.model.Count
import com.online.course.model.Data
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.ProvidersFiltersDialog
import retrofit2.Call
import retrofit2.Response

class ProvidersFiltersPresenterImpl(private val dialog: ProvidersFiltersDialog) :
    Presenter.ProvidersFiltersPresenter {

    override fun getCategories() {
        val categories = ApiService.apiClient!!.getCategories()
        dialog.addNetworkRequest(categories)
        categories.enqueue(object : CustomCallback<Data<Count<Category>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getCategories()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Category>>>,
                response: Response<Data<Count<Category>>>
            ) {
                if (response.body() != null)
                    dialog.onCategoriesRecevied(response.body()!!)
            }

        })
    }
}