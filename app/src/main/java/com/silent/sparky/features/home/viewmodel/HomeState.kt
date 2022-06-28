package com.silent.sparky.features.home.viewmodel

import com.silent.core.podcast.Podcast
import com.silent.core.videos.Video
import com.silent.sparky.features.home.data.PodcastHeader

sealed class HomeState {
    data class HomeChannelsRetrieved(val podcastHeaders: ArrayList<PodcastHeader>) : HomeState()
    data class HomeLivesRetrieved(val podcasts: ArrayList<Podcast>) : HomeState()
    object HomeFetched: HomeState()
    object PreferencesNotSet : HomeState()
    object ValidManager : HomeState()
    object InvalidManager : HomeState()
    object HomeError : HomeState()
    object HomeLiveError : HomeState()
}
