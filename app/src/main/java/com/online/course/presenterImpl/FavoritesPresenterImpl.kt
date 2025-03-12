package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.FavoritesFrag
import retrofit2.Call
import retrofit2.Response

class FavoritesPresenterImpl(private val frag: FavoritesFrag) : Presenter.FavoritesPresenter {

    override fun getFavorites() {
        val favoritesReq = ApiService.apiClient!!.getFavorites()
        frag.addNetworkRequest(favoritesReq)
        favoritesReq.enqueue(object : CustomCallback<Data<Count<Favorite>>> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        getFavorites()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Count<Favorite>>>,
                    response: Response<Data<Count<Favorite>>>
                ) {
                    if (response.body() != null) {
                        frag.onFavoritesReceived(response.body()!!.data!!.items)
                    }
                }

            })
    }

    override fun removeFromFavorite(addToFav: AddToFav, adapterPosition: Int) {
        ApiService.apiClient!!.addRemoveFromFavorite(addToFav)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        removeFromFavorite(addToFav, adapterPosition)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onItemRemoved(response.body()!!, adapterPosition)
                    }
                }

            })
    }
}