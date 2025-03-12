package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.ProfilePhoto
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.SettingsFrag
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class SettingsPresenterImpl(private val frag: SettingsFrag) :
    Presenter.SettingsPresenter {

    override fun uploadPhoto(path: String) {
        val file = File(path)
        val fileBody = file.asRequestBody()
        val filePart: MultipartBody.Part =
            MultipartBody.Part.createFormData("profile_image", file.name, fileBody)
        ApiService.apiClient!!.changeProfileImage(filePart)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        uploadPhoto(path)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onProfileSaved(response.body()!!)
                    }
                }

            })
    }
}