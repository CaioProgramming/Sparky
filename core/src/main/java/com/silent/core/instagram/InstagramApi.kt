package com.silent.core.instagram

import retrofit2.http.GET
import retrofit2.http.Path

interface InstagramApi {


    @GET("{user}/channel")
    suspend fun getUserInfo(@Path("user") username: String): InstagramResponse

}