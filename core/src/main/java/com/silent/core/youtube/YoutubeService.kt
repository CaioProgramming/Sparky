package com.silent.core.youtube

import com.silent.core.twitch.ChannelDetailsResponse
import com.silent.core.youtube.data.YoutubeVideoResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val API_URL = "https://www.googleapis.com/youtube/v3/"

class YoutubeService {

    private val retroFitService: Retrofit by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(interceptor).build()
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(API_URL).build()
    }


    suspend fun getChannelDetails(programID: String): ChannelDetailsResponse {
        val youtubeApi = retroFitService.create(YoutubeApi::class.java)
        return youtubeApi.getChannelDetails(channelId = programID)
    }

    suspend fun getChannelSections(
        channelID: String
    ): ChannelSectionResponse {
        val youtubeApi = retroFitService.create(YoutubeApi::class.java)
        return youtubeApi.getChannelSections(channelID)
    }

    suspend fun getPlaylistVideos(
        playlistId: String
    ): PlaylistItemResponse {
        val youtubeApi = retroFitService.create(YoutubeApi::class.java)
        return youtubeApi.getChannelUploads(playlistId = playlistId)
    }

    suspend fun getChannelLiveStatus(channelID: String): SearchResponse {
        val youtubeApi = retroFitService.create(YoutubeApi::class.java)
        return youtubeApi.searchChannelLive(channelId = channelID)
    }

    suspend fun getVideoDetails(videoID: String): YoutubeVideoResponse {
        val youtubeApi = retroFitService.create(YoutubeApi::class.java)
        return youtubeApi.getVideoDetails(videoID = videoID)
    }

}