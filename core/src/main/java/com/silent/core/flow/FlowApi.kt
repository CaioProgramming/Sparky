package com.silent.core.flow

import com.silent.core.flow.data.FlowProfileResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FlowApi {

    @GET("user/profile/{username}")
    suspend fun getFlowProfile(
        @Path("username")
        username: String
    ): FlowProfileResponse


}