package com.silent.core

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class RetrofitService(private val apiUrl: String) {
    protected val retroFitService: Retrofit by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient().newBuilder().addInterceptor(interceptor).build()
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(apiUrl).build()
    }

}