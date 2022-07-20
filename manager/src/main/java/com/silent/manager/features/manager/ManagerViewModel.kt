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
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.utilities.format
import com.silent.ilustriscore.core.utilities.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

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
    var uploads: List<Video>? = null
    var cuts: List<Video>? = null

    override fun deleteData(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uploads?.forEach {
                videoService.deleteData(it.id)
            }
            cuts?.forEach {
                cutService.deleteData(it.id)
            }
            super.deleteData(id)
        }

    }

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
                     fetchEpisodesAndCuts(podcast.id, podcast.uploads, podcast.cuts) { epsAndCuts ->
                         viewModelScope.launch(Dispatchers.IO) {
                             epsAndCuts.first.forEach { video ->
                                 videoService.editData(video)
                             }
                             epsAndCuts.second.forEach { video ->
                                 cutService.editData(video)
                             }
                             managerState.postValue(ManagerState.PodcastUpdated(podcast, epsAndCuts.first.size, epsAndCuts.second.size, index))
                         }

                    }

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

    private suspend fun getDateToFetch(isCuts: Boolean = false, podcastId: String, fetchDate: (String) -> Unit) {
        if (!isCuts) {
            val videoService = videoService.getPodcastVideos(podcastId)
            if (videoService is ServiceResult.Success) {
                val videos = (videoService.data as ArrayList<Video>).sortedByDescending { it.publishedAt }
                val firstVideo = videos.first()
                val todayDate = GregorianCalendar.getInstance()
                val videoDate = GregorianCalendar.getInstance()
                videoDate.time = firstVideo.publishedAt
                when (todayDate.compareTo(videoDate)) {
                    -1 -> {
                        fetchDate(videoDate.time.formatDate("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"))

                    }
                    0 -> {
                        fetchDate(videos.last().publishedAt.formatDate("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"))
                    }

                }
            }
        } else {
            val cutServiceRequest = cutService.getPodcastCuts(podcastId)
            if (cutServiceRequest is ServiceResult.Success) {
                val videos = (cutServiceRequest.data as ArrayList<Video>).sortedByDescending { it.publishedAt }
                val firstVideo = videos.first()
                val todayDate = GregorianCalendar.getInstance()
                val videoDate = GregorianCalendar.getInstance()
                videoDate.time = firstVideo.publishedAt
                when (todayDate.compareTo(videoDate)) {
                    -1 -> {
                       fetchDate(videoDate.time.formatDate("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"))
                    }
                    0 -> {
                      fetchDate(videos.last().publishedAt.formatDate("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"))
                    }

                }
            }
        }
    }

    private suspend fun fetchEpisodesAndCuts(
        podcastId: String,
        uploads: String,
        cuts: String,
        mappedVideosAndCutsListener: (Pair<ArrayList<Video>, ArrayList<Video>>) -> Unit
    ){
        val mappedCuts = ArrayList<Video>()
        val mappedUploads = ArrayList<Video>()

        getDateToFetch(false, podcastId) { date ->
            viewModelScope.launch(Dispatchers.IO) {
                val uploads = youtubeService.getPlaylistVideos(uploads, beforeDate =  date)
                uploads.items.toString().length
                uploads.items.forEach {
                    mappedUploads.add(videoMapper.mapVideoSnippet(it.snippet, podcastId))
                }
            }
        }

        getDateToFetch(true, podcastId) { date ->
            viewModelScope.launch(Dispatchers.IO) {
                val cuts = youtubeService.getPlaylistVideos(cuts, beforeDate = date)
                cuts.items.forEach {
                    mappedCuts.add(videoMapper.mapVideoSnippet(it.snippet, podcastId))
                }
                mappedVideosAndCutsListener(Pair(mappedUploads, mappedCuts))
            }

        }

    }

}