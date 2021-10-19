package com.silent.core.youtube

import com.silent.core.BuildConfig
import com.silent.core.twitch.ChannelDetailsResponse
import com.silent.core.youtube.PlaylistItemResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val DATA_SNIPPET_QUERY = "snippet"
private const val DATA_CONTENT_DETAILS_QUERY = "contentDetails"
private const val MAX_UPLOADS_REQUIRED = 20
interface YoutubeApi {

    @GET("channels")
    suspend fun getChannelDetails(
        @Query("part") snippetData: String? = DATA_SNIPPET_QUERY,
        @Query("part") contentDetails: String? = DATA_CONTENT_DETAILS_QUERY,
        @Query("id") channelId: String,
        @Query("key") apiKey: String? = BuildConfig.YOUTUBE_KEY) : ChannelDetailsResponse

    @GET("playlistItems")
    suspend fun getChannelUploads(
        @Query("part") snippet: String? = DATA_SNIPPET_QUERY,
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int = MAX_UPLOADS_REQUIRED,
        @Query("key") apiKey: String? = BuildConfig.YOUTUBE_KEY) : PlaylistItemResponse

}