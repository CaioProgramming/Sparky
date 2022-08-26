package com.silent.manager.features.podcast

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.*
import com.silent.core.videos.CutService
import com.silent.core.videos.Video
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.core.youtube.response.PlaylistResource
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PodcastManagerViewModel(
    application: Application,
    override val service: PodcastService,
    private val videoService: VideoService,
    private val cutService: CutService,
    private val youtubeService: YoutubeService,
    private val videoMapper: VideoMapper
) : BaseViewModel<Podcast>(application) {

    sealed class PodcastManagerState {
        data class CutsAndUploadsRetrieved(val sections: programSections) : PodcastManagerState()
        object PodcastUpdateRequest : PodcastManagerState()
        object VideoDeleted : PodcastManagerState()
        data class EpisodesUpdated(var count: Int) : PodcastManagerState()
        data class CutsUpdated(var count: Int) : PodcastManagerState()
        data class PlayslitDeleted(var message: String) : PodcastManagerState()
    }

    val podcastManagerState = MutableLiveData<PodcastManagerState>()

    fun updatePodcastData(
        podcast: Podcast,
        updateClips: Boolean = false,
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
                updateEpisodesAndCuts(podcast)
                editData(podcast)
            }
        }
    }


    private fun isVideoOlder(lastVideoDate: Date, lastItemDate: Date) = lastVideoDate < lastItemDate


    private fun updatePlaylist(
        playlistId: String,
        lastVideoDate: Date,
        nextPageToken: String? = null,
        retrievePlaylist: (List<PlaylistResource>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var youtubeRequest =
                youtubeService.getPlaylistVideos(playlistId, pageToken = nextPageToken)
            var lastItemResource = youtubeRequest.items.last()
            if (isVideoOlder(lastVideoDate, lastItemResource.snippet.publishedAt)) {
                updatePlaylist(
                    playlistId,
                    lastVideoDate,
                    nextPageToken = youtubeRequest.nextPageToken,
                    retrievePlaylist
                )
            } else {
                retrievePlaylist(youtubeRequest.items)
            }
        }
    }

    private fun saveVideos(
        podcastId: String,
        isCuts: Boolean,
        playlistResources: List<PlaylistResource>
    ) {
        viewModelScope.launch {
            playlistResources.forEachIndexed { index, playlistResource ->
                val video = videoMapper.mapVideoSnippet(playlistResource.snippet, podcastId)
                if (isCuts) {
                    cutService.editData(video)
                } else {
                    videoService.editData(video)
                }
                if (index == playlistResources.lastIndex) {
                    podcastManagerState.postValue(
                        if (!isCuts) PodcastManagerState.EpisodesUpdated(
                            playlistResources.size
                        ) else PodcastManagerState.CutsUpdated(playlistResources.size)
                    )
                }
            }
        }
    }

    private fun updateEpisodesAndCuts(podcast: Podcast) {
        viewModelScope.launch(Dispatchers.IO) {
            val videos = videoService.getPodcastVideos(podcast.id) as ServiceResult.Success
            val lastVideo = (videos.data as ArrayList<Video>).minByOrNull { it.publishedAt }
            lastVideo?.let {
                updatePlaylist(podcast.uploads, lastVideo.publishedAt.toDate()) {
                    saveVideos(podcast.id, false, it)
                }
            }

            val cuts = cutService.getPodcastCuts(podcast.id) as ServiceResult.Success
            val lastCut = (cuts.data as ArrayList<Video>).minByOrNull { it.publishedAt }
            lastCut?.let {
                updatePlaylist(podcast.cuts, lastCut.publishedAt.toDate()) {
                    saveVideos(podcast.id, true, it)
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
                val uploadList =
                    (uploads.success.data as ArrayList<Video>).sortedByDescending { it.publishedAt }
                headers.add(
                    PodcastHeader(
                        "Episódios",
                        subTitle = "${uploadList.size} episódios salvos.",
                        type = HeaderType.VIDEOS,
                        videos = ArrayList(uploadList),
                        orientation = RecyclerView.HORIZONTAL
                    )
                )
            }
            if (uploads.isSuccess) {
                val cutsList =
                    (cuts.success.data as ArrayList<Video>).sortedByDescending { it.publishedAt }
                headers.add(
                    PodcastHeader(
                        "Cortes",
                        subTitle = "${cutsList.size} cortes salvos.",
                        type = HeaderType.VIDEOS,
                        videos = ArrayList(cutsList),
                        orientation = RecyclerView.HORIZONTAL
                    )
                )
            }

            podcastManagerState.postValue(PodcastManagerState.CutsAndUploadsRetrieved(headers))
        }
    }

    fun deletePlaylist(videos: ArrayList<Video>?, playlistName: String) {
        val isCuts = playlistName.contains("Cortes")
        viewModelScope.launch(Dispatchers.IO) {
            videos?.forEach {
                if (!isCuts) {
                    videoService.deleteData(it.id)
                } else {
                    cutService.deleteData(it.id)
                }
            }
            podcastManagerState.postValue(PodcastManagerState.PlayslitDeleted(if (isCuts) "${videos?.size} removidos" else "${videos?.size} uploads removidos"))
        }

    }

    fun deleteVideo(id: String, isCuts: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!isCuts) {
                videoService.deleteData(id)
            } else {
                cutService.deleteData(id)
            }
            podcastManagerState.postValue(PodcastManagerState.VideoDeleted)
        }
    }
}