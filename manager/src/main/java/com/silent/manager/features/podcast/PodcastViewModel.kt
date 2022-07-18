package com.silent.manager.features.podcast

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.videos.CutService
import com.silent.core.videos.Video
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.utilities.format
import com.silent.ilustriscore.core.utilities.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class PodcastViewModel(
    application: Application,
    override val service: PodcastService,
    private val videoService: VideoService,
    private val cutService: CutService,
    private val youtubeService: YoutubeService,
    private val videoMapper: VideoMapper
) : BaseViewModel<Podcast>(application) {

    sealed class PodcastManagerState() {
        object PodcastUpdateRequest : PodcastManagerState()
        data class EpisodesUpdated(var count: Int) : PodcastManagerState()
        data class CutsUpdated(var count: Int) : PodcastManagerState()
    }

    val podcastManagerState = MutableLiveData<PodcastManagerState>()

    fun updatePodcastData(podcast: Podcast, updateClips: Boolean = false, requireOldVideos: Boolean = false) {
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
                updateEpisodesAndCuts(podcast, requireOldVideos)
                editData(podcast)
            }
        }
    }



    private suspend fun updateEpisodesAndCuts(podcast: Podcast, requireOldVideos: Boolean = false) {
        if (requireOldVideos) {
            val videoRequest = videoService.getPodcastVideos(podcast.id)
            if (videoRequest is ServiceResult.Success) {
                val lastVideo = (videoRequest.data as ArrayList<Video>).minByOrNull { it.publishedAt }
                lastVideo?.let { video ->
                    val fetchDate = video.publishedAt.formatDate("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                    val uploads = youtubeService.getPlaylistVideos(podcast.uploads, 100, fetchDate)
                    uploads.items.forEachIndexed { index, playlistResource ->
                        val video = videoMapper.mapVideoSnippet(playlistResource.snippet, podcast.id)
                        videoService.editData(video)

                        if (index == uploads.items.lastIndex) {
                            podcastManagerState.postValue(PodcastManagerState.EpisodesUpdated(uploads.items.size))
                        }
                    }
                }
            }
            val cutRequest = cutService.getPodcastCuts(podcast.id)
            if (cutRequest is ServiceResult.Success) {
                val lastVideo = (cutRequest.data as ArrayList<Video>).minByOrNull { it.publishedAt }
                lastVideo?.let { video ->
                    val fetchDate = video.publishedAt.formatDate("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                    val uploads = youtubeService.getPlaylistVideos(podcast.uploads, 100, fetchDate)
                    uploads.items.forEachIndexed { index, playlistResource ->
                        val video = videoMapper.mapVideoSnippet(playlistResource.snippet, podcast.id)
                        videoService.editData(video)
                        if (index == uploads.items.lastIndex) {
                            podcastManagerState.postValue(PodcastManagerState.EpisodesUpdated(uploads.items.size))
                        }
                    }

                }
            }
        }




    }
}