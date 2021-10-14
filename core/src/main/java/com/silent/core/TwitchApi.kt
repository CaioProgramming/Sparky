package com.silent.core

import retrofit2.http.GET
import retrofit2.http.Path

interface TwitchApi {

    @GET("{channel_name}/channel/{channel_id}/videos")
    fun getProgramStreams(@Path("channel_name") channelName: String,
                          @Path("channel_id") channelId: String)


}