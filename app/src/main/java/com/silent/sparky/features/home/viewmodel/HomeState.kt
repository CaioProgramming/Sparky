package com.silent.sparky.features.home.viewmodel

import com.silent.sparky.data.PodcastHeader

sealed class HomeState {
    data class HomeChannelRetrieved(val podcastHeader: PodcastHeader) : HomeState()
    object HomeError : HomeState()
}
