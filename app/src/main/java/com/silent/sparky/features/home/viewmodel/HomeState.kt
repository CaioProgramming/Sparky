package com.silent.sparky.features.home.viewmodel

import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastHeader

sealed class HomeState {
    data class HomeSearchRetrieved(val podcastHeader: ArrayList<PodcastHeader>) : HomeState()
    data class HomeChannelsRetrieved(val podcastHeaders: ArrayList<PodcastHeader>) : HomeState()
    data class HomeLivesRetrieved(val podcasts: ArrayList<Podcast>) : HomeState()
    object HomeFetched : HomeState()
    object ValidManager : HomeState()
    object InvalidManager : HomeState()
    object HomeError : HomeState()
    object HomeLiveError : HomeState()
    object LoadingSearch : HomeState()
}

sealed class UserState {
    object NewNotificationsState : UserState()
}

sealed class PreferencesState {
    object PreferencesNotSet : PreferencesState()
    object WarningNotShowed : PreferencesState()
    object PreferencesDone : PreferencesState()

}
