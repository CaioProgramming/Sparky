package com.silent.sparky.features.home.viewmodel

import com.silent.sparky.data.PodcastHeader
import com.silent.sparky.features.home.data.LiveHeader

sealed class HomeState {
    data class HomeChannelRetrieved(val podcastHeader: PodcastHeader) : HomeState()
    data class HomeLivesRetrieved(val podcasts: ArrayList<LiveHeader>) : HomeState()
    object HomeError : HomeState()
    object HomeLiveError : HomeState()
}
