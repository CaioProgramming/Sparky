package com.silent.core.instagram

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InstagramApi {


    @GET("{user}/")
    suspend fun getUserInfo(
        @Path("user") username: String,
        @Query("__a") query: Int = 1
    ): InstagramResponse

}