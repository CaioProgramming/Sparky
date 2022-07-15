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
import com.silent.core.videos.VideoService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.sparky.features.cuts.data.PodcastCutHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CutsViewModel(
    application: Application,
    override val service: CutService,
   private  val podcastService: PodcastService
) : BaseViewModel<Podcast>(application) {
    private val preferencesService = PreferencesService(application)
    val cutsState = MutableLiveData<CutsState>()

    fun fetchCuts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val podcastFilter = ArrayList<String>()
                val preferencesPodcasts = preferencesService.getStringSetValue(PODCASTS_PREFERENCES)
                preferencesPodcasts?.let { podcastFilter.addAll(it) }
                val podcasts = podcastService.getAllData().success.data as podcasts
                if (preferencesPodcasts?.isNotEmpty() == true) {
                    val filteredPodcasts = podcasts.filter { podcastFilter.contains(it.id) }
                    val sortedPodcasts = filteredPodcasts.sortedByDescending { it.subscribe }
                    val cutHeaders = ArrayList<PodcastCutHeader>()
                    sortedPodcasts.forEachIndexed { index, podcast ->
                        when (val cutsRequest = service.query(podcast.id, "podcastId")) {
                            is ServiceResult.Error -> {
                                Log.e(javaClass.simpleName, "fetchCuts: error /n $cutsRequest \n ")
                                sendErrorState(cutsRequest.errorException)
                            }
                            is ServiceResult.Success -> {
                                val channelUploads = cutsRequest.data as ArrayList<Video>
                                channelUploads.map { it.podcast = podcast }
                                cutHeaders.add(PodcastCutHeader(podcast, ArrayList(channelUploads.sortedByDescending { it.publishedAt })))
                            }
                        }
                        if (index == sortedPodcasts.lastIndex) {
                            cutsState.postValue(CutsState.CutsRetrieved(cutHeaders))
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
                        cutHeaders.add(PodcastCutHeader(podcast, ArrayList(channelUploads.sortedByDescending { it.publishedAt })))
                        if (index == podcasts.lastIndex) {
                            cutsState.postValue(CutsState.CutsRetrieved(cutHeaders))
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