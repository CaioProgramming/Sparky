package com.silent.sparky.features.live.viewmodel

import com.silent.core.podcast.Podcast

sealed class LiveViewState {
    data class PodcastData(val podcast: Podcast) : LiveViewState()
}
