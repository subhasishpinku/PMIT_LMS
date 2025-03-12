package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.Data
import com.online.course.model.Points
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.RewardPointsFrag
import retrofit2.Call
import retrofit2.Response

class RewardPointsPresenterImpl(private val frag: RewardPointsFrag) :
    Presenter.RewardPointsPresenter {

    override fun getPoints() {
        val pointsReq = ApiService.apiClient!!.getPoints()
        frag.addNetworkRequest(pointsReq)
        pointsReq.enqueue(object : CustomCallback<Data<Points>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getPoints()
                }
            }

            override fun onResponse(call: Call<Data<Points>>, res: Response<Data<Points>>) {
                if (res.body() != null) {
                    frag.onPointsReceived(res.body()!!.data!!)
                }
            }
        })
    }
}