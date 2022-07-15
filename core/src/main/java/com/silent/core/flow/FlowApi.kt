package com.silent.core.flow

import com.silent.core.flow.data.FlowProfileResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FlowApi {

    @GET("profile")
    suspend fun getFlowProfile(
        @Query("username")
        username: String
    ): FlowProfileResponse


}