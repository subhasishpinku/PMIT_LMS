package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.UsersOrganizationsFrag
import retrofit2.Call
import retrofit2.Response

class ProvidersPresenterImpl(private val frag: UsersOrganizationsFrag) :
    Presenter.ProvidersPresenter {

    override fun getProviders(
        type: UsersOrganizationsFrag.ProviderType,
        map: List<KeyValuePair>?
    ) {
        var url = "providers/"

        url += when (type) {
            UsersOrganizationsFrag.ProviderType.CONSULTANTS -> {
                "consultations"
            }
            UsersOrganizationsFrag.ProviderType.ORGANIZATIONS -> {
                "organizations"
            }
            UsersOrganizationsFrag.ProviderType.INSTRUCTORS -> {
                "instructors"
            }
        }

        if (map != null && map.isNotEmpty()) {
            url += "?"
            for ((index, item) in map.withIndex()) {
                url += "${item.key}=${item.value}"
                if (index != map.size - 1) {
                    url += "&"
                }
            }
        }

        val providersReq = ApiService.apiClient!!.getProviders(url)
        frag.addNetworkRequest(providersReq)
        providersReq.enqueue(object : CustomCallback<Data<Count<User>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getProviders(type, map)
                }
            }

            override fun onResponse(
                call: Call<Data<Count<User>>>,
                response: Response<Data<Count<User>>>
            ) {
                if (response.body() != null) {
                    frag.onProvidersReceived(response.body()!!.data!!)
                }
            }

        })
    }
}