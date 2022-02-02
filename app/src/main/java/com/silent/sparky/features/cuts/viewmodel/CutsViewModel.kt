package com.silent.sparky.features.cuts.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.podcast.podcasts
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CutsViewModel(application: Application) : BaseViewModel<Podcast>(application) {
    override val service = PodcastService()
    private val youtubeService = YoutubeService()
    val cutsState = MutableLiveData<CutsState>()

    fun fetchCuts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val podcasts = service.getAllData().success.data as podcasts
                podcasts.forEachIndexed { index, podcast ->
                    delay((100 * index).toLong())
                    val channelUploads = youtubeService.getPlaylistVideos(podcast.cuts)
                    cutsState.postValue(
                        CutsState.CutsRetrieved(
                            podcast.cuts,
                            ArrayList(channelUploads.items)
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                cutsState.postValue(CutsState.CutsError)
            }
        }
    }


}