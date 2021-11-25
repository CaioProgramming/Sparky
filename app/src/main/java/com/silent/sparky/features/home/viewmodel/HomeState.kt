package com.silent.sparky.features.home.viewmodel

import com.silent.sparky.features.podcast.data.PodcastHeader

sealed class HomeState {
    data class HomeChannelRetrieved(val podcastHeader: PodcastHeader) : HomeState()
    object HomeError : HomeState()
}
