package com.silent.newpodcasts.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silent.core.data.podcast.Podcast
import com.silent.core.service.YoutubeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewPodcastViewModel: ViewModel() {

    val youtubeService = YoutubeService()
    sealed class NewPodcastState {
        data class UploadNewPodcastState(val podcast: Podcast) : NewPodcastState()
        object NewPodcastError: NewPodcastState()
    }
    val newPodcastState = MutableLiveData<NewPodcastState>()
    val podcast = Podcast()


    fun getRelatedChannels(channelID: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val channelSectionListResponse = youtubeService.getChannelRelatedChannels(channelID)

        }
    }








}