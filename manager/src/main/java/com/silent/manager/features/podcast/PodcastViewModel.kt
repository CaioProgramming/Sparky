package com.silent.manager.features.podcast

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.videos.CutService
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PodcastViewModel(application: Application) : BaseViewModel<Podcast>(application) {

    sealed class PodcastManagerState() {
        object PodcastUpdateRequest : PodcastManagerState()
        data class EpisodesUpdated(var count: Int) : PodcastManagerState()
        data class CutsUpdated(var count: Int) : PodcastManagerState()
    }

    override val service = PodcastService()
    private val videoService = VideoService()
    private val cutService = CutService()
    private val videoMapper = VideoMapper()
    private val youtubeService = YoutubeService()
    val podcastManagerState = MutableLiveData<PodcastManagerState>()
    fun updatePodcastData(podcast: Podcast, updateClips: Boolean = false) {

        viewModelScope.launch(Dispatchers.IO) {
            podcastManagerState.postValue(PodcastManagerState.PodcastUpdateRequest)
            val channel = youtubeService.getChannelDetails(podcast.youtubeID).items.first()
            podcast.apply {
                youtubeID = channel.id
                iconURL = channel.snippet.thumbnails.high.url
                subscribe = channel.statistics.subscriberCount
                views = channel.statistics.viewCount
                uploads = channel.contentDetails.relatedPlaylists.uploads
            }
            if (!updateClips) {
                editData(podcast)
            } else {
                val uploads = youtubeService.getPlaylistVideos(podcast.uploads)
                val cuts = youtubeService.getPlaylistVideos(podcast.cuts)
                uploads.items.forEachIndexed { index, playlistResource ->
                    val video = videoMapper.mapVideoSnippet(playlistResource.snippet, podcast.id)
                    videoService.addData(video)
                    if (index == uploads.items.lastIndex) {
                        podcastManagerState.postValue(PodcastManagerState.EpisodesUpdated(uploads.items.size))
                    }
                }
                cuts.items.forEachIndexed { index, playlistResource ->
                    val video = videoMapper.mapVideoSnippet(playlistResource.snippet, podcast.id)
                    cutService.editData(video)
                    if (index == uploads.items.lastIndex) {
                        podcastManagerState.postValue(PodcastManagerState.CutsUpdated(uploads.items.size))
                    }
                }
                editData(podcast)
            }
        }
    }


}