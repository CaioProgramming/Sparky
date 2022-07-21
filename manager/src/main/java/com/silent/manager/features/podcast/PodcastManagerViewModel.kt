package com.silent.manager.features.podcast

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.*
import com.silent.core.videos.*
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.utilities.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PodcastManagerViewModel(
    application: Application,
    override val service: PodcastService,
    private val videoService: VideoService,
    private val cutService: CutService,
    private val youtubeService: YoutubeService,
    private val videoMapper: VideoMapper
) : BaseViewModel<Podcast>(application) {

    sealed class PodcastManagerState() {
        data class CutsAndUploadsRetrieved(val sections: programSections): PodcastManagerState()
        object PodcastUpdateRequest : PodcastManagerState()
        data class EpisodesUpdated(var count: Int) : PodcastManagerState()
        data class CutsUpdated(var count: Int) : PodcastManagerState()
    }

    val podcastManagerState = MutableLiveData<PodcastManagerState>()

    fun updatePodcastData(
        podcast: Podcast,
        updateClips: Boolean = false,
        requireOldVideos: Boolean = false
    ) {
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
                val lastVideo =
                    (videoRequest.data as ArrayList<Video>).minByOrNull { it.publishedAt }
                lastVideo?.let { video ->
                    val fetchDate = video.publishedAt.formatDate("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                    val uploads = youtubeService.getPlaylistVideos(podcast.uploads, 100, fetchDate)
                    uploads.items.forEachIndexed { index, playlistResource ->
                        val video =
                            videoMapper.mapVideoSnippet(playlistResource.snippet, podcast.id)
                        videoService.editData(video)

                        if (index == uploads.items.lastIndex) {
                            podcastManagerState.postValue(
                                PodcastManagerState.EpisodesUpdated(
                                    uploads.items.size
                                )
                            )
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
                        val video =
                            videoMapper.mapVideoSnippet(playlistResource.snippet, podcast.id)
                        videoService.editData(video)
                        if (index == uploads.items.lastIndex) {
                            podcastManagerState.postValue(
                                PodcastManagerState.EpisodesUpdated(
                                    uploads.items.size
                                )
                            )
                        }
                    }

                }
            }
        } else {
            val uploads = youtubeService.getPlaylistVideos(podcast.uploads, 100)
            val cuts = youtubeService.getPlaylistVideos(podcast.cuts, 100)
            uploads.items.forEachIndexed { index, playlistResource ->
                val video = videoMapper.mapVideoSnippet(playlistResource.snippet, podcast.id)
                videoService.editData(video)

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
        }
    }

    fun getVideosAndCuts(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val headers = ArrayList<PodcastHeader>()
            val uploads = videoService.getPodcastVideos(id)
            val cuts = cutService.getPodcastCuts(id)
            if (uploads.isSuccess) {
                val uploadList = (uploads.success.data as ArrayList<Video>).sortedByDescending { it.publishedAt }
                headers.add(PodcastHeader("Episódios", subTitle = "${uploadList.size} episódios salvos.", type = HeaderType.VIDEOS, videos = ArrayList(uploadList), orientation = RecyclerView.HORIZONTAL))
            }
            if (uploads.isSuccess) {
                val cutsList = (cuts.success.data as ArrayList<Video>).sortedByDescending { it.publishedAt }
                headers.add(PodcastHeader("Cortes", subTitle = "${cutsList.size} cortes salvos." , type = HeaderType.VIDEOS, videos =  ArrayList(cutsList), orientation = RecyclerView.HORIZONTAL))
            }

            podcastManagerState.postValue(PodcastManagerState.CutsAndUploadsRetrieved(headers))
        }
    }
}