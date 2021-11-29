package com.silent.core.instagram

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val API_URL = "https://www.instagram.com/"


class InstagramService {

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

    suspend fun getUserInfo(userName: String): InstagramResponse {
        val instagramApi = retroFitService.create(InstagramApi::class.java)
        return instagramApi.getUserInfo(userName)
    }
}