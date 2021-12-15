package com.silent.sparky.features.live.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ViewModelBaseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveViewModel : BaseViewModel<Podcast>() {
    override val service = PodcastService()
    private val youtubeService = YoutubeService()
    val liveViewState = MutableLiveData<LiveViewState>()

    fun getPodcastInfo(videoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val videoData = youtubeService.getVideoDetails(videoId)
                val channelID = videoData.items[0].snippet.channelId
                val podcast = service.query("youtubeID", channelID).success.data[0] as Podcast
                liveViewState.postValue(LiveViewState.PodcastData(podcast))
            } catch (e: Exception) {
                viewModelState.postValue(ViewModelBaseState.ErrorState(DataException()))
            }
        }
    }


}