package com.silent.manager.features.manager

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.podcast.podcasts
import com.silent.core.videos.CutService
import com.silent.core.videos.Video
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManagerViewModel(
    application: Application,
    override val service: PodcastService,
    private val youtubeService: YoutubeService,
    private val videoService: VideoService,
    private val cutService: CutService,
    private val videoMapper: VideoMapper
) : BaseViewModel<Podcast>(application) {

    sealed class ManagerState {
        object UpdateComplete : ManagerState()
        data class UpdatingPodcasts(val podcasts: podcasts) : ManagerState()
        data class PodcastUpdated(val podcast: Podcast, val newUploadsCount: Int, val newCutsCount: Int, val index: Int) : ManagerState()
        data class UpdateError(val podcast: Podcast) : ManagerState()
    }

    val managerState = MutableLiveData<ManagerState>()

    fun updatePodcastsEpisodesAndCuts() {
        viewModelScope.launch(Dispatchers.IO) {
            val podcasts = (service.getAllData().success.data as ArrayList<Podcast>).sortedByDescending { it.subscribe }
            podcasts.forEach {
                it.updating = true
            }
            managerState.postValue(ManagerState.UpdatingPodcasts(ArrayList(podcasts)))
            podcasts.forEachIndexed { index, podcast ->
                try {
                    val channel = youtubeService.getChannelDetails(podcast.youtubeID).items.first()
                    podcast.apply {
                        youtubeID = channel.id
                        iconURL = channel.snippet.thumbnails.high.url
                        subscribe = channel.statistics.subscriberCount
                        views = channel.statistics.viewCount
                        uploads = channel.contentDetails.relatedPlaylists.uploads
                    }
                    val epsAndCuts = fetchEpisodesAndCuts(podcast.id, podcast.uploads, podcast.cuts)
                    epsAndCuts.first.forEach { video ->
                        videoService.editData(video)
                    }
                    epsAndCuts.second.forEach { video ->
                        cutService.editData(video)
                    }
                    managerState.postValue(ManagerState.PodcastUpdated(podcast, epsAndCuts.first.size, epsAndCuts.second.size, index))

                    if (index == podcasts.lastIndex) {
                        managerState.postValue(ManagerState.UpdateComplete)
                    }
                } catch (e: Exception) {
                    Log.e(
                        javaClass.simpleName,
                        "updatePodcastsEpisodesAndCuts: Error updating ${podcast.name}"
                    )
                    e.printStackTrace()
                    managerState.postValue(ManagerState.UpdateError(podcast))
                }
            }
        }

    }

    private suspend fun fetchEpisodesAndCuts(
        podcastId: String,
        uploads: String,
        cuts: String
    ): Pair<ArrayList<Video>, ArrayList<Video>> {
        val uploads = youtubeService.getPlaylistVideos(uploads)
        val cuts = youtubeService.getPlaylistVideos(cuts)
        val mappedUploads = ArrayList<Video>()
        val mappedCuts = ArrayList<Video>()
        uploads.items.toString().length

        uploads.items.forEach {
            mappedUploads.add(videoMapper.mapVideoSnippet(it.snippet, podcastId))
        }
        cuts.items.forEach {
            mappedCuts.add(videoMapper.mapVideoSnippet(it.snippet, podcastId))
        }

        return Pair(mappedUploads, mappedCuts)
    }

}