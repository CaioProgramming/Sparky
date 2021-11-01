package com.silent.core.api

import com.silent.core.BuildConfig
import com.silent.core.data.model.youtube.ChannelSectionListResponse
import com.silent.core.data.model.youtube.SearchResponse
import com.silent.core.twitch.ChannelDetailsResponse
import com.silent.core.youtube.PlaylistItemResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val DATA_SNIPPET_QUERY = "snippet"
private const val DATA_CONTENT_DETAILS_QUERY = "contentDetails"
private const val DATA_BRANDING_QUERY = "brandingSettings"
private const val DATA_VIDEO_TYPE_QUERY = "video"
private const val DATA_EVENT_TYPE_LIVE_QUERY = "live"
private const val MAX_UPLOADS_REQUIRED = 20
interface YoutubeApi {

    @GET("search")
    suspend fun searchChannelLive(
        @Query("part") snippetData: String? = DATA_SNIPPET_QUERY,
        @Query("channelId") channelId: String,
        @Query("type") searchType: String? = DATA_VIDEO_TYPE_QUERY,
        @Query("eventType") eventType: String? = DATA_EVENT_TYPE_LIVE_QUERY,
        @Query("key") apiKey: String? = BuildConfig.YOUTUBE_KEY

    ) : SearchResponse

    @GET("channels")
    suspend fun getChannelDetails(
        @Query("part") snippetData: String? = DATA_SNIPPET_QUERY,
        @Query("part") contentDetails: String? = DATA_CONTENT_DETAILS_QUERY,
        @Query("part") brandingDetails: String? = DATA_BRANDING_QUERY,
        @Query("id") channelId: String,
        @Query("key") apiKey: String? = BuildConfig.YOUTUBE_KEY) : ChannelDetailsResponse

    @GET("playlistItems")
    suspend fun getChannelUploads(
        @Query("part") snippet: String? = DATA_SNIPPET_QUERY,
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int = MAX_UPLOADS_REQUIRED,
        @Query("key") apiKey: String? = BuildConfig.YOUTUBE_KEY) : PlaylistItemResponse

    @GET("channelSections")
    suspend fun getRelatedChannels(
        @Query("part") snippet: String? = DATA_SNIPPET_QUERY,
        @Query("part") contentDetails: String? = DATA_CONTENT_DETAILS_QUERY,
        @Query("channelId") channelId: String
    ) : ChannelSectionListResponse
}