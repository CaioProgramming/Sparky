package com.silent.sparky.features.cuts.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.podcast.podcasts
import com.silent.core.preferences.PreferencesService
import com.silent.core.utils.PODCASTS_PREFERENCES
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CutsViewModel(application: Application) : BaseViewModel<Podcast>(application) {
    override val service = PodcastService()
    private val youtubeService = YoutubeService()
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
                        val channelUploads = youtubeService.getPlaylistVideos(podcast.cuts)
                        cutsState.postValue(
                            CutsState.CutsRetrieved(
                                podcast.cuts,
                                ArrayList(channelUploads.items)
                            )
                        )
                    }
                } else {
                    podcasts.forEachIndexed { index, podcast ->
                        val channelUploads = youtubeService.getPlaylistVideos(podcast.cuts)
                        cutsState.postValue(
                            CutsState.CutsRetrieved(
                                podcast.cuts,
                                ArrayList(channelUploads.items)
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