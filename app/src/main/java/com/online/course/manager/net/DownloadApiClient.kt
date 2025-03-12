package com.online.course.manager.net

import retrofit2.http.Streaming
import retrofit2.http.GET
import retrofit2.http.Url
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Path

interface DownloadApiClient {
    @Streaming
    @GET
    fun download(@Url url: String): Call<ResponseBody>
}