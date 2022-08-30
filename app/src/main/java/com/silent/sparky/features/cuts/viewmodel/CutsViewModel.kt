package com.silent.sparky.features.cuts.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.podcast.podcasts
import com.silent.core.preferences.PreferencesService
import com.silent.core.utils.PODCASTS_PREFERENCES
import com.silent.core.videos.CutService
import com.silent.core.videos.Video
import com.silent.core.videos.VideoType
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.sparky.features.cuts.data.PodcastCutHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CutsViewModel(
    application: Application,
    override val service: CutService,
    private val podcastService: PodcastService
) : BaseViewModel<Podcast>(application) {
    private val preferencesService = PreferencesService(application)
    val cutsState = MutableLiveData<CutsState>()

    fun fetchCuts(podcastFilters: List<String> = emptyList()) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val podcastFilter = ArrayList<String>()
                val preferencesPodcasts = preferencesService.getStringSetValue(PODCASTS_PREFERENCES)
                preferencesPodcasts?.let { podcastFilter.addAll(it) }
                val podcasts =
                    (podcastService.getAllData().success.data as podcasts).sortedByDescending { it.subscribe }
                val cuts = ArrayList<Video>()
                if (preferencesPodcasts?.isNotEmpty() == true) {
                    val filteredPodcasts = podcasts.filter { podcastFilter.contains(it.id) }
                    val sortedPodcasts = filteredPodcasts.sortedByDescending { it.subscribe }
                    if (podcastFilters.isNotEmpty()) sortedPodcasts.filter {
                        podcastFilters.contains(
                            it.id
                        )
                    }
                    sortedPodcasts.forEachIndexed { index, podcast ->
                        when (val cutsRequest = service.query(podcast.id, "podcastId")) {
                            is ServiceResult.Error -> {
                                Log.e(javaClass.simpleName, "fetchCuts: error /n $cutsRequest \n ")
                                sendErrorState(cutsRequest.errorException)
                            }
                            is ServiceResult.Success -> {
                                val podcastCuts = cutsRequest.data as ArrayList<Video>
                                podcastCuts.map { it.podcast = podcast }
                                val sortedVideos = podcastCuts.sortedByDescending { it.publishedAt }
                                val cutsArray = ArrayList(sortedVideos)
                                cutsArray.forEachIndexed { index, video ->
                                    if (index % 5 == 0) {
                                        cutsArray[index].videoType = VideoType.MEDIUM
                                    }
                                    cutsArray.first().videoType = VideoType.BIG
                                    if (cutsArray.lastIndex % 2 != 0 || index % 5 == 0) {
                                        cutsArray.last().videoType = VideoType.BIG
                                    }
                                }
                                cuts.addAll(sortedVideos)
                            }
                        }
                        if (index == sortedPodcasts.lastIndex) {
                            cutsState.postValue(
                                CutsState.CutsRetrieved(
                                    cuts,
                                    ArrayList(sortedPodcasts)
                                )
                            )
                        }
                    }
                } else {
                    val cutHeaders = ArrayList<PodcastCutHeader>()
                    podcasts.forEachIndexed { index, podcast ->
                        val channelUploads = service.query(
                            podcast.id,
                            "podcastId"
                        ).success.data as ArrayList<Video>
                        channelUploads.map { it.podcast = podcast }
                        val sortedPodcasts =
                            channelUploads.sortedByDescending { it.publishedAt }.apply {
                                first().videoType = VideoType.BIG
                            }
                        cuts.addAll(sortedPodcasts)
                        cutHeaders.add(
                            PodcastCutHeader(
                                podcast,
                                ArrayList(channelUploads.sortedByDescending { it.publishedAt })
                            )
                        )
                        if (index == podcasts.lastIndex) {
                            cutsState.postValue(CutsState.CutsRetrieved(cuts, ArrayList(podcasts)))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                cutsState.postValue(CutsState.CutsError)
            }
        }
    }


}