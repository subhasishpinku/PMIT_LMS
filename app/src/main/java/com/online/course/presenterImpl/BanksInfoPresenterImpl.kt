package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Count
import com.online.course.model.Data
import com.online.course.model.SystemBankAccount
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.BanksInfoFrag
import retrofit2.Call
import retrofit2.Response

class BanksInfoPresenterImpl(private val frag: BanksInfoFrag) : Presenter.BanksInfoPresenter {

    override fun getBanksInfo() {
        val bankInfosReq = ApiService.apiClient!!.getBankInfos()
        frag.addNetworkRequest(bankInfosReq)
        bankInfosReq.enqueue(object : CustomCallback<Data<Count<SystemBankAccount>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getBanksInfo()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Count<SystemBankAccount>>>,
                    response: Response<Data<Count<SystemBankAccount>>>
                ) {
                    if (response.body() != null) {
                        frag.onInfosReceived(response.body()!!.data!!.items)
                    }
                }

            })
    }
}