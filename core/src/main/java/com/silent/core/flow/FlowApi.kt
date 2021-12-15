package com.silent.core.flow

import com.silent.core.flow.data.FlowLivesResponse
import com.silent.core.flow.data.FlowProfileResponse
import retrofit2.http.*

private const val LIVES_FILTER = "landing"
interface FlowApi {

    @GET("user/profile/{username}")
    suspend fun getFlowProfile(
        @Path("username")
        username: String
    ): FlowProfileResponse


    @FormUrlEncoded
    @POST("episodes/list")
    suspend fun getLives(@Field("filter") filter: String = LIVES_FILTER): FlowLivesResponse


}