package com.silent.sparky.features.podcast

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.firebase.FirebaseService
import com.silent.core.podcast.Host
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.preferences.PreferencesService
import com.silent.core.utils.PODCASTS_PREFERENCES
import com.silent.core.videos.CutService
import com.silent.core.videos.Video
import com.silent.core.videos.VideoService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.sparky.features.home.data.PodcastHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class PodcastViewModel(
    application: Application,
    override val service: PodcastService,
    private val videoService: VideoService,
    private val cutService: CutService,
    private val preferencesService: PreferencesService,
    private val firebaseService: FirebaseService
) : BaseViewModel<Podcast>(application) {

    sealed class PodcastState {
        object PodcastFailedState : PodcastState()
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
            val guests: List<Host>,
            val position: Int,
            val podcast: Podcast
        ) : ScheduleState()
    }

    val channelState = MutableLiveData<PodcastState>()
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
            channelState.postValue(PodcastState.UpdateFavorite(isFavorite))
        }
    }

    private fun handleServiceResult(serviceResult: ServiceResult<DataException, String>) {
        when(serviceResult) {
            is ServiceResult.Error -> {
                Log.e(javaClass.simpleName, "handleServiceResult: Error -> ${serviceResult.errorException}", )
            }
            is ServiceResult.Success -> {
                Log.i(javaClass.simpleName, "handleServiceResult: Success -> ${serviceResult.data}")
            }
        }
    }

    fun getChannelData(podcastID: String) {
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
                            podcast.name
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
                            podcast.name,
                        )
                    )
                }
                val sortedGuests = podcast.weeklyGuests.sortedBy { it.comingDate }
                podcast.weeklyGuests = ArrayList(sortedGuests)
                val preferencesPodcasts = preferencesService.getStringSetValue(PODCASTS_PREFERENCES)

                channelState.postValue(
                    PodcastState.PodcastDataRetrieved(
                        podcast,
                        headers,
                        preferencesPodcasts?.contains(podcastID) == true
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                channelState.postValue(PodcastState.PodcastFailedState)
            }
        }
    }

    fun checkSchedule(podcast: Podcast) {
        val calendar = Calendar.getInstance()
        podcast.weeklyGuests.forEachIndexed { index, host ->
            val hostCalendar = Calendar.getInstance()
            hostCalendar.time = host.comingDate
            if (hostCalendar[Calendar.DAY_OF_MONTH] == calendar[Calendar.DAY_OF_MONTH]) {
                scheduleState.postValue(
                    ScheduleState.TodayGuestState(
                        podcast.weeklyGuests,
                        index,
                        podcast
                    )
                )
            }
        }
    }

    fun clearState() {
        channelState.value = null
        viewModelState.value = null
        scheduleState.value = null
    }

}