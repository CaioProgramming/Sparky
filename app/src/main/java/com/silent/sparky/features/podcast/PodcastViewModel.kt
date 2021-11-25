package com.silent.sparky.features.podcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.program.Podcast
import com.silent.core.program.PodcastService
import com.silent.core.youtube.PlaylistResource
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.sparky.features.podcast.data.PodcastHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PodcastViewModel : BaseViewModel<Podcast>() {

    private val youtubeService = YoutubeService()
    override val service = PodcastService()


    sealed class ChannelState {
        object ChannelFailedState : ChannelState()
        data class ChannelUploadsRetrieved(
            val videos: List<PlaylistResource>,
            val playlistId: String
        ) : ChannelState()

        data class ChannelDataRetrieved(
            val podcast: Podcast,
            val uploads: PodcastHeader,
            val cuts: PodcastHeader
        ) : ChannelState()
    }

    val channelState = MutableLiveData<ChannelState>()

    private fun getHeader(
        title: String,
        playlistId: String,
        videos: List<PlaylistResource>,
        orientation: Int
    ) = PodcastHeader(
        title = title,
        playlistId = playlistId,
        videos = videos,
        orientation = orientation
    )

    fun getChannelData(podcastID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val podcast = service.getSingleData(podcastID).success.data
                val channelsResponse = youtubeService.getChannelDetails(podcast.youtubeID).items[0]
                val uploads =
                    youtubeService.getChannelUploads(channelsResponse.contentDetails.relatedPlaylists.uploads)
                val cuts = youtubeService.getChannelUploads(podcast.cuts)
                podcast.apply {
                    iconURL = channelsResponse.snippet.thumbnails.high.url
                }
                val uploadHeader = getHeader(
                    "Últimos episódios",
                    channelsResponse.contentDetails.relatedPlaylists.uploads,
                    uploads.items,
                    RecyclerView.HORIZONTAL
                )
                val cutsHeader = getHeader(
                    "Úlitmos cortes",
                    podcast.cuts,
                    cuts.items,
                    RecyclerView.HORIZONTAL
                )
                channelState.postValue(
                    ChannelState.ChannelDataRetrieved(
                        podcast,
                        uploadHeader,
                        cutsHeader
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                channelState.postValue(ChannelState.ChannelFailedState)
            }
        }
    }


    fun getChannelVideos(playlistId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val channelUploads = youtubeService.getChannelUploads(playlistId)
                print("Videos data -> $channelUploads")
                channelState.postValue(
                    ChannelState.ChannelUploadsRetrieved(
                        channelUploads.items,
                        playlistId
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                channelState.postValue(ChannelState.ChannelFailedState)
            }
        }
    }


}