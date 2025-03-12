package com.online.course.manager.net

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.model.BaseResponse
import com.online.course.ui.MainActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiService {

    private const val AUTHORIZATION = "Authorization"
    private const val API_KEY = "X-API-Key"
    private const val LOCALE_KEY = "X-Locale"
    private var mRetrofitService: Retrofit? = null
    private var mApiClient: ApiClient? = null
    private var mToken: String? = null
    private var mLocale: String = BuildVars.DefaultLang.CODE
    var activity: MainActivity? = null

    val downloadingRequestsResponses: HashMap<Int, ProgressResponseBody> = HashMap()

    fun createApiService() {
        mToken = null
        buildApiService()
    }

    fun createAuthorizedApiService(token: String) {
        mToken = token

        buildApiService()
    }

    fun createApiServiceWithLocale(locale: String) {
        mLocale = locale

        buildApiService()
    }

    private fun getOkHttpDownloadClientBuilder(
        progressListener: OnDownloadProgressListener?,
        downloaderId: Int?
    ): OkHttpClient.Builder {
        val httpClientBuilder = OkHttpClient.Builder()

        // You might want to increase the timeout
        httpClientBuilder.connectTimeout(20, TimeUnit.SECONDS)
        httpClientBuilder.writeTimeout(20, TimeUnit.SECONDS)
        httpClientBuilder.readTimeout(5, TimeUnit.MINUTES)
        httpClientBuilder.addInterceptor(Interceptor { chain: Interceptor.Chain ->

            val request = chain.request()
            val newReq = request.newBuilder()
                .addHeader(API_KEY, BuildVars.API_KEY)

            if (mToken != null) {
                newReq.addHeader(AUTHORIZATION, "Bearer $mToken")
            }

            val originalResponse = chain.proceed(newReq.build())

            val progressResponseBody =
                ProgressResponseBody(originalResponse.body, progressListener, downloaderId)
            if (downloaderId != null) {
                downloadingRequestsResponses[downloaderId] = progressResponseBody
            }

            val req = originalResponse.newBuilder()
                .addHeader(API_KEY, BuildVars.API_KEY)
                .body(progressResponseBody)

            if (mToken != null) {
                req.addHeader(AUTHORIZATION, "Bearer $mToken")
            }

            req.build()
        })
        return httpClientBuilder
    }

    fun getDownloadApiClient(
        url: String,
        progressListener: OnDownloadProgressListener?,
        downloaderId: Int?
    ): DownloadApiClient {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpDownloadClientBuilder(progressListener, downloaderId).build())
            .build()
        return retrofit.create(DownloadApiClient::class.java)
    }

    private fun buildApiService() {
        mApiClient = null

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        var builder =
            OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(
                Interceptor { chain: Interceptor.Chain ->
                    val request = chain.request()
                    val newReq = request.newBuilder()
                        .addHeader(LOCALE_KEY, mLocale)
                        .addHeader(API_KEY, BuildVars.API_KEY)

                    if (mToken != null) {
                        newReq.addHeader(AUTHORIZATION, "Bearer $mToken")
                    }

                    val rsp = chain.proceed(newReq.build())
                    if (rsp.code == 401 && activity != null) {
                        App.logout(activity!!)
                    }
                    rsp
                }
            )

        builder.connectTimeout(20, TimeUnit.SECONDS)
        builder.writeTimeout(0, TimeUnit.SECONDS)
        builder.readTimeout(10, TimeUnit.MINUTES)

        if (BuildVars.LOGS_ENABLED) {
            builder = builder.addInterceptor(interceptor)
        }
        val client = builder.build()
        val gson = GsonBuilder().setLenient().serializeNulls().create()

        mRetrofitService = Retrofit.Builder()
            .baseUrl(BuildVars.BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .build()
    }

    @JvmStatic
    val apiClient: ApiClient?
        get() {
            if (mRetrofitService != null) {
                if (mApiClient == null) {
                    mApiClient = mRetrofitService!!.create(ApiClient::class.java)
                }
            } else {
                createApiService()
                mApiClient = mRetrofitService!!.create(ApiClient::class.java)
            }
            return mApiClient
        }
}