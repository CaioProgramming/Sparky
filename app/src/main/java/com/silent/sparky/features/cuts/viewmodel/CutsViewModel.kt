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
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.model.ViewModelBaseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CutsViewModel(application: Application) : BaseViewModel<Podcast>(application) {
    override val service = PodcastService()
    private val cutService = CutService()
    private val preferencesService = PreferencesService(application)
    val cutsState = MutableLiveData<CutsState>()

    fun fetchCuts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val podcastFilter = ArrayList<String>()
                val preferencesPodcasts = preferencesService.getStringSetValue(PODCASTS_PREFERENCES)
                preferencesPodcasts?.let { podcastFilter.addAll(it) }
                val podcasts = service.getAllData().success.data as podcasts
                if (preferencesPodcasts?.isNotEmpty() == true) {
                    val filteredPodcasts = podcasts.filter { podcastFilter.contains(it.id) }
                    val sortedPodcasts = filteredPodcasts.sortedByDescending { it.subscribe }
                    sortedPodcasts.forEachIndexed { index, podcast ->
                        when(val cutsRequest = cutService.query(podcast.id, "podcastId")) {
                            is ServiceResult.Error -> {
                                Log.e(javaClass.simpleName, "fetchCuts: error /n $cutsRequest \n ", )
                                sendErrorState(cutsRequest.errorException)
                            }
                            is ServiceResult.Success -> {
                                val channelUploads = cutsRequest.data as ArrayList<Video>
                                channelUploads.map { it.podcast = podcast }
                                cutsState.postValue(
                                    CutsState.CutsRetrieved(
                                        podcast.cuts,
                                        ArrayList(channelUploads.sortedByDescending { it.publishedAt })
                                    )
                                )
                            }
                        }

                    }
                } else {
                    podcasts.forEachIndexed { index, podcast ->
                        val channelUploads = cutService.query(podcast.id, "podcastId").success.data as ArrayList<Video>
                        cutsState.postValue(
                            CutsState.CutsRetrieved(
                                podcast.cuts,
                                ArrayList(channelUploads)
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                cutsState.postValue(CutsState.CutsError)
            }
        }
    }


}