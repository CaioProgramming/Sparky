package com.silent.core.flow

import com.silent.core.RetrofitService
import com.silent.core.flow.data.FlowProfileResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val API_URL = "https://users-flow3r-2eqj3fl3la-ue.a.run.app//v1/"

class FlowService : RetrofitService(API_URL) {
    suspend fun getProfile(username: String): FlowProfileResponse {
        val flowApi = retroFitService.create(FlowApi::class.java)
        return flowApi.getFlowProfile(username)
    }
}