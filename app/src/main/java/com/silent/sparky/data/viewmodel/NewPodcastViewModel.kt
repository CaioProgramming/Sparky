package com.silent.sparky.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silent.core.data.program.Podcast

class NewPodcastViewModel: ViewModel() {

    sealed class NewPodcastState {
        data class UploadNewPodcastState(val podcast: Podcast) : NewPodcastState()
        object NewPodcastError: NewPodcastState()
    }
    val newPodcastState = MutableLiveData<NewPodcastState>()
    val podcast = Podcast()









}