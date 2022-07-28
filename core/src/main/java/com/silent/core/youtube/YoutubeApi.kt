package com.silent.core.youtube

import com.silent.core.BuildConfig
import com.silent.core.youtube.response.PlaylistItemResponse
import com.silent.core.youtube.response.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val DATA_SNIPPET_QUERY = "snippet"
private const val DATA_CONTENT_DETAILS_QUERY = "contentDetails"
private const val DATA_BRANDING_QUERY = "brandingSettings"
private const val DATA_STATISTICS_QUERY = "statistics"
private const val DATA_VIDEO_TYPE_QUERY = "video"
private const val DATA_EVENT_TYPE_LIVE_QUERY = "live"
private const val MAX_UPLOADS_REQUIRED = 50

interface YoutubeApi {

    @GET("search")
    suspend fun searchChannelLive(
        @Query("part") snippetData: String? = DATA_SNIPPET_QUERY,
        @Query("channelId") channelId: String,
        @Query("type") searchType: String? = DATA_VIDEO_TYPE_QUERY,
        @Query("eventType") eventType: String? = DATA_EVENT_TYPE_LIVE_QUERY,
        @Query("maxResults") maxResults: Int = 1,
        @Query("key") apiKey: String? = BuildConfig.YOUTUBE_KEY,


    ): SearchResponse

    @GET("channels")
    suspend fun getChannelDetails(
        @Query("part") snippetData: String? = DATA_SNIPPET_QUERY,
        @Query("part") contentDetails: String? = DATA_CONTENT_DETAILS_QUERY,
        @Query("part") brandingDetails: String? = DATA_BRANDING_QUERY,
        @Query("part") statistics: String? = DATA_STATISTICS_QUERY,
        @Query("id") channelId: String,
        @Query("key") apiKey: String? = BuildConfig.YOUTUBE_KEY
    ): ChannelDetailsResponse

    @GET("channels")
    suspend fun getChannelDetailsForUserName(
        @Query("part") snippetData: String? = DATA_SNIPPET_QUERY,
        @Query("part") contentDetails: String? = DATA_CONTENT_DETAILS_QUERY,
        @Query("part") brandingDetails: String? = DATA_BRANDING_QUERY,
        @Query("forUsername") channelName: String,
        @Query("key") apiKey: String? = BuildConfig.YOUTUBE_KEY
    ): ChannelDetailsResponse

    @GET("playlistItems")
    suspend fun getPlaylistVideos(
        @Query("part") snippet: String? = DATA_SNIPPET_QUERY,
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int = MAX_UPLOADS_REQUIRED,
        @Query("publishedAfter") beforeDate: String? = null,
        @Query("key") apiKey: String? = BuildConfig.YOUTUBE_KEY
    ): PlaylistItemResponse



    @GET("channelSections")
    suspend fun getChannelSections(
        @Query("channelId") channelId: String,
        @Query("part") snippet: String? = DATA_SNIPPET_QUERY,
        @Query("part") contentDetails: String? = DATA_CONTENT_DETAILS_QUERY,
        @Query("key") apiKey: String? = BuildConfig.YOUTUBE_KEY
    ): ChannelSectionResponse

}