package com.silent.core.stickers

import com.silent.core.RetrofitService
import com.silent.core.stickers.response.StickersResponse

private const val STICKERS_URL = "https://stickers-nv99-2eqj3fl3la-ue.a.run.app//v2/"

class StickersService : RetrofitService(STICKERS_URL) {
    suspend fun getUserStickers(username: String): StickersResponse {
        val apiCall = retroFitService.create(StickersApi::class.java)
        return apiCall.getUserStickers(username)
    }


}