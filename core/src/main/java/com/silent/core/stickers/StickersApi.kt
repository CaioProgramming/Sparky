package com.silent.core.stickers

import com.silent.core.stickers.response.StickersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StickersApi {

    @GET("badges/user_badges")
    suspend fun getUserStickers(@Query("username") username: String): StickersResponse

}