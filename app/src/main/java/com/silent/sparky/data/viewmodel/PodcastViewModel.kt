package com.silent.sparky.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silent.core.service.YoutubeService
import com.silent.core.twitch.ChannelResource
import com.silent.core.youtube.PlaylistResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PodcastViewModel: ViewModel() {

    private val youtubeService = YoutubeService()

    sealed class PodcastState {
        object PodcastFailedState : PodcastState()
        data class PodcastLiveState(val videoID: String): PodcastState()
        object PodcastOfflineState: PodcastState()
        data class PodcastUploadsRetrieved(val videos:  List<PlaylistResource>, val playlistId: String): PodcastState()
        data class PodcastCutsRetrieved(val videos:  List<PlaylistResource>, val playlistId: String): PodcastState()
        data class PodcastDataRetrieved(val channelDetails: ChannelResource): PodcastState()
    }

   val podcastState = MutableLiveData<PodcastState>()

    fun getChannelHome(channelID: String, videosRetrieved: (List<PlaylistResource>, String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val channelResponse = youtubeService.getChannelDetails(channelID)
            val channelUploads = youtubeService.getChannelUploads(channelResponse.items[0].contentDetails.relatedPlaylists.uploads)
            videosRetrieved(channelUploads.items, channelResponse.items[0].contentDetails.relatedPlaylists.uploads)
        }
    }

    fun getChannelData(channelID: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val channelsResponse = youtubeService.getChannelDetails(channelID)
            podcastState.postValue(PodcastState.PodcastDataRetrieved(channelsResponse.items[0]))
        }
    }

    fun getChannelCuts(cutsID: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val channelUploads = youtubeService.getChannelUploads(cutsID)
                podcastState.postValue(
                    PodcastState.PodcastCutsRetrieved(
                        channelUploads.items,
                        cutsID
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                podcastState.postValue(PodcastState.PodcastFailedState)
            }
        }
    }

    fun getChannelVideos(playlistId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val channelUploads = youtubeService.getChannelUploads(playlistId)
                print("Videos data -> $channelUploads" )
                podcastState.postValue(
                    PodcastState.PodcastUploadsRetrieved(
                        channelUploads.items,
                        playlistId
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                podcastState.postValue(PodcastState.PodcastFailedState)
            }
        }
    }

    fun checkChannelLive(channelID: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val searchResponse = youtubeService.getChannelLiveStatus(channelID)
            if (searchResponse.items.isNotEmpty()) {
                podcastState.postValue(PodcastState.PodcastLiveState(searchResponse.items[0].id.videoId))
            } else {
                podcastState.postValue(PodcastState.PodcastOfflineState)
            }
        }
    }

}