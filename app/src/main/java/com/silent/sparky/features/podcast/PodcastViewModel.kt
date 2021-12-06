package com.silent.sparky.features.podcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.youtube.PlaylistResource
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.sparky.data.PodcastHeader
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
                val uploads = youtubeService.getPlaylistVideos(podcast.uploads)
                val cuts = youtubeService.getPlaylistVideos(podcast.cuts)
                val uploadHeader = getHeader(
                    "Últimos episódios",
                    podcast.uploads,
                    uploads.items,
                    RecyclerView.HORIZONTAL
                )
                val cutsHeader = getHeader(
                    "Úlitmos cortes",
                    podcast.cuts,
                    cuts.items,
                    RecyclerView.VERTICAL
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

}