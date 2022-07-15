package com.silent.core.stickers

import com.silent.core.stickers.response.StickersResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface StickersApi {

    @GET("badges/return/{username}/list")
   suspend fun getUserStickers(@Path("username") username: String) : StickersResponse

}