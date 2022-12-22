package com.silent.sparky.features.podcast

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.firebase.FirebaseService
import com.silent.core.podcast.HeaderType
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastHeader
import com.silent.core.podcast.PodcastService
import com.silent.core.preferences.PreferencesService
import com.silent.core.utils.TOKEN_PREFERENCES
import com.silent.core.videos.CutService
import com.silent.core.videos.Video
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


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
        subtitle: String,
        podcast: Podcast,
        type: HeaderType
    ) = PodcastHeader(
        title = title,
        playlistId = playlistId,
        videos = ArrayList(videos),
        orientation = orientation,
        highLightColor = highlightColor,
        subTitle = subtitle,
        seeMore = true,
        podcast = podcast,
        type = type
    )

    fun favoritePodcast(podcast: Podcast, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            getUser()?.let {
                if (isFavorite) {
                    podcast.subscribers.add(it.uid)
                } else {
                    podcast.subscribers.remove(it.uid)
                }
                editData(podcast)
            }
        }
    }

    fun getPodcastData(podcastID: String, video: Video? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val headers = ArrayList<PodcastHeader>()
                val podcast = service.getSingleData(podcastID).success.data as Podcast
                val uploads =
                    (videoService.getPodcastVideos(podcastID).success.data as ArrayList<Video>).sortedByDescending { it.publishedAt }
                val cuts =
                    (cutService.getPodcastCuts(podcastID).success.data as ArrayList<Video>).sortedByDescending { it.publishedAt }

                if (uploads.isNotEmpty()) {
                    uploads.map { it.podcast = podcast }
                    headers.add(
                        getHeader(
                            "Episódios",
                            podcast.uploads,
                            uploads,
                            if (cuts.isEmpty()) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL,
                            podcast.highLightColor,
                            "${uploads.size} episódios disponíveis.",
                            podcast,
                            HeaderType.VIDEOS
                        )
                    )
                }
                if (cuts.isNotEmpty()) {
                    cuts.map { it.podcast = podcast }
                    headers.add(
                        getHeader(
                            "Cortes do ${podcast.name}",
                            podcast.cuts,
                            cuts,
                            RecyclerView.VERTICAL,
                            podcast.highLightColor,
                            "${cuts.size} cortes disponíveis.",
                            podcast,
                            HeaderType.CUTS
                        )
                    )
                }
                podcastState.postValue(
                    PodcastState.PodcastDataRetrieved(
                        podcast,
                        headers,
                        podcast.subscribers.contains(
                            preferencesService.getStringValue(
                                TOKEN_PREFERENCES
                            )
                        )
                    )
                )
                Log.i(javaClass.simpleName, "getPodcastData: podcastData -> $podcast\n$headers")
                var lastVideo = uploads.firstOrNull {
                    val videoDate = Calendar.getInstance().apply {
                        time = it.publishedAt
                    }
                    val todayDate = Calendar.getInstance()
                    videoDate[Calendar.DAY_OF_YEAR] == todayDate[Calendar.DAY_OF_YEAR]
                }
                video?.let {
                    lastVideo = it
                }
                checkLive(lastVideo, podcast)
            } catch (e: Exception) {
                e.printStackTrace()
                podcastState.postValue(PodcastState.PodcastFailedState)
            }
        }
    }

    private fun checkLive(video: Video?, podcast: Podcast) {
        video?.let {
            val zone = ZoneId.of("America/Sao_Paulo")
            val localDateTime = LocalDateTime.now()
            val zonedDateTime = ZonedDateTime.of(localDateTime, zone)
            val hour = zonedDateTime.hour
            if (podcast.liveTime == hour || possibleLive(hour, podcast.liveTime)) {
                scheduleState.postValue(ScheduleState.TodayGuestState(it))
            }
        } ?: run {
            viewModelScope.launch(Dispatchers.IO) {
                val zone = ZoneId.of("America/Sao_Paulo")
                val localDateTime = LocalDateTime.now()
                val zonedDateTime = ZonedDateTime.of(localDateTime, zone)
                val hour = zonedDateTime.hour
                if (podcast.liveTime == hour || possibleLive(hour, podcast.liveTime)) {
                    val liveRequest = youtubeService.getChannelLiveStatus(podcast.youtubeID)
                    if (liveRequest.items.isNotEmpty()) {
                        val mapper = VideoMapper()
                        val snippetItem = liveRequest.items.first()
                        val video = mapper.mapLiveSnippet(
                            snippetItem.id.videoId,
                            snippetItem.snippet,
                            podcast.id,
                            podcast = podcast
                        )
                        scheduleState.postValue(ScheduleState.TodayGuestState(video))
                    }
                }
            }
        }
    }

    private fun possibleLive(hour: Int, liveHour: Int) =
        (liveHour + 1) == hour || (liveHour + 2) == hour || (liveHour + 3 == hour)

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
                            "${queryVideos.size} resultados.",
                            podcast,
                            HeaderType.VIDEOS
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
                            "${queryCuts.size} resultados.",
                            podcast,
                            HeaderType.CUTS
                        )
                    )
                }
            }
            podcastState.postValue(PodcastState.RetrieveSearch(headers))
        }
    }

}