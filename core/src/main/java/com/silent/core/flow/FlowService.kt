package com.silent.core.flow

import com.silent.core.flow.data.FlowProfileResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val API_URL = "https://flow3r-api-master-2eqj3fl3la-ue.a.run.app/v3/"

class FlowService {

    private val retroFitService: Retrofit by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(interceptor).build()
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(API_URL).build()
    }

    suspend fun getProfile(username: String): FlowProfileResponse {
        val flowApi = retroFitService.create(FlowApi::class.java)
        return flowApi.getFlowProfile(username)
    }


}