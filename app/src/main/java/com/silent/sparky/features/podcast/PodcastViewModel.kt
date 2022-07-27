package com.silent.sparky.features.podcast

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.firebase.FirebaseService
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastHeader
import com.silent.core.podcast.PodcastService
import com.silent.core.preferences.PreferencesService
import com.silent.core.utils.PODCASTS_PREFERENCES
import com.silent.core.videos.CutService
import com.silent.core.videos.Video
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PodcastViewModel(
    application: Application,
    override val service: PodcastService,
    private val videoService: VideoService,
    private val cutService: CutService,
    private val preferencesService: PreferencesService,
    private val firebaseService: FirebaseService,
    private val youtubeService: YoutubeService
) : BaseViewModel<Podcast>(application) {

    sealed class PodcastState {
        object PodcastFailedState : PodcastState()
        data class RetrieveSearch(val headers: ArrayList<PodcastHeader>) : PodcastState()
        data class PodcastDataRetrieved(
            val podcast: Podcast,
            val headers: ArrayList<PodcastHeader>,
            val isFavorite: Boolean
        ) : PodcastState()

        data class UpdateHeader(val position: Int, val videos: List<Video>, val lastIndex: Int) :
            PodcastState()

        data class UpdateFavorite(val isFavorite: Boolean) : PodcastState()
    }

    sealed class ScheduleState {
        data class TodayGuestState(
            val video: Video
        ) : ScheduleState()
    }

    val podcastState = MutableLiveData<PodcastState>()
    val scheduleState = MutableLiveData<ScheduleState>()

    private fun getHeader(
        title: String,
        playlistId: String,
        videos: List<Video>,
        orientation: Int,
        highlightColor: String,
        subtitle: String
    ) = PodcastHeader(
        title = title,
        playlistId = playlistId,
        videos = ArrayList(videos),
        orientation = orientation,
        highLightColor = highlightColor,
        subTitle = subtitle,
        seeMore = true
    )

    fun favoritePodcast(podcastID: String, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val userPodcasts = ArrayList<String>()
            val preferedPodcasts = preferencesService.getStringSetValue(PODCASTS_PREFERENCES)
            preferedPodcasts?.forEach {
                userPodcasts.add(it)
            }

            if (isFavorite) {
                userPodcasts.add(podcastID)
                firebaseService.subscribeToTopic(podcastID, ::handleServiceResult)
            } else {
                userPodcasts.remove(podcastID)
                firebaseService.unsubscribeTopic(podcastID, ::handleServiceResult)
            }

            preferencesService.editPreference(PODCASTS_PREFERENCES, userPodcasts.toSet())
            podcastState.postValue(PodcastState.UpdateFavorite(isFavorite))
        }
    }

    private fun handleServiceResult(serviceResult: ServiceResult<DataException, String>) {
        when (serviceResult) {
            is ServiceResult.Error -> {
                Log.e(
                    javaClass.simpleName,
                    "handleServiceResult: Error -> ${serviceResult.errorException}",
                )
            }
            is ServiceResult.Success -> {
                Log.i(javaClass.simpleName, "handleServiceResult: Success -> ${serviceResult.data}")
            }
        }
    }

    fun getPodcastData(podcastID: String, video: Video? = null) {
        clearState()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val headers = ArrayList<PodcastHeader>()
                val podcast = service.getSingleData(podcastID).success.data as Podcast
                val uploads =
                    videoService.getPodcastVideos(podcastID).success.data as ArrayList<Video>
                val cuts = cutService.getPodcastCuts(podcastID).success.data as ArrayList<Video>

                if (uploads.isNotEmpty()) {
                    uploads.map { it.podcast = podcast }
                    headers.add(
                        getHeader(
                            "Episódios",
                            podcast.uploads,
                            uploads.sortedByDescending { it.publishedAt },
                            if (cuts.isEmpty()) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL,
                            podcast.highLightColor,
                            "${uploads.size} episódios disponíveis."
                        )
                    )
                }
                if (cuts.isNotEmpty()) {
                    cuts.map { it.podcast = podcast }
                    headers.add(
                        getHeader(
                            "Cortes do ${podcast.name}",
                            podcast.cuts,
                            cuts.sortedByDescending { it.publishedAt },
                            RecyclerView.VERTICAL,
                            podcast.highLightColor,
                            "${cuts.size} cortes disponíveis.",
                        )
                    )
                }
                val preferencesPodcasts = preferencesService.getStringSetValue(PODCASTS_PREFERENCES)
                podcastState.postValue(
                    PodcastState.PodcastDataRetrieved(
                        podcast,
                        headers,
                        preferencesPodcasts?.contains(podcastID) == true
                    )
                )
                checkLive(video)
            } catch (e: Exception) {
                e.printStackTrace()
                podcastState.postValue(PodcastState.PodcastFailedState)
            }
        }
    }

    fun checkLive(video: Video?) {
        video?.let {
            scheduleState.postValue(ScheduleState.TodayGuestState(it))
        }
    }

    fun clearState() {
        podcastState.value = null
        viewModelState.value = null
        scheduleState.value = null
    }

    fun searchEpisodesAndCuts(podcast: Podcast, query: String) {
        if (query.isEmpty()) {
            getPodcastData(podcast.id)
        }
        viewModelScope.launch {
            val headers = ArrayList<PodcastHeader>()
            val videoRequest = videoService.getPodcastVideos(podcast.id)
            if (videoRequest is ServiceResult.Success) {
                val videos = videoRequest.data as ArrayList<Video>
                val queryVideos = videos.filter {
                    it.title.contains(query, true) || it.description.contains(
                        query,
                        true
                    )
                }
                if (queryVideos.isNotEmpty()) {
                    headers.add(
                        0,
                        getHeader(
                            "Episódios encontrados",
                            podcast.uploads,
                            queryVideos,
                            RecyclerView.VERTICAL,
                            podcast.highLightColor,
                            "${queryVideos.size} resultados."
                        )
                    )
                }
            }
            val cutRequest = cutService.getPodcastCuts(podcast.id)
            if (cutRequest is ServiceResult.Success) {
                val cuts = cutRequest.data as ArrayList<Video>
                val queryCuts = cuts.filter {
                    it.title.contains(query, true) || it.description.contains(
                        query,
                        true
                    )
                }
                if (queryCuts.isNotEmpty()) {
                    headers.add(
                        getHeader(
                            "Cortes encontrados",
                            podcast.cuts,
                            queryCuts,
                            RecyclerView.VERTICAL,
                            podcast.highLightColor,
                            "${queryCuts.size} resultados."
                        )
                    )
                }
            }
            podcastState.postValue(PodcastState.RetrieveSearch(headers))
        }
    }

}