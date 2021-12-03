package com.silent.sparky.features.cuts.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CutsViewModel : BaseViewModel<Podcast>() {
    override val service = PodcastService()
    private val youtubeService = YoutubeService()
    val cutsState = MutableLiveData<CutsState>()

    fun fetchCuts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val podcasts = service.getAllData().success.data
                podcasts.forEach {
                    val channelUploads = youtubeService.getPlaylistVideos(it.cuts)
                    cutsState.postValue(
                        CutsState.CutsRetrieved(
                            it.cuts,
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