package com.silent.manager.states

import com.silent.core.instagram.InstagramUserResponse
import com.silent.core.podcast.Host
import com.silent.core.podcast.Podcast
import com.silent.manager.features.newpodcast.fragments.youtube.PodcastsHeader

sealed class NewPodcastState {
    data class RelatedCutsRetrieved(val podcastsHeader: List<PodcastsHeader>) : NewPodcastState()
    data class RelatedPodcastsRetrieved(val podcastsHeader: List<PodcastsHeader>) :
        NewPodcastState()

    data class ValidPodcast(val podcast: Podcast) : NewPodcastState()
    object PodcastUpdated : NewPodcastState()
    object InvalidPodcast : NewPodcastState()
}

sealed class HostState {
    data class HostInstagramRetrieve(val instagramUser: InstagramUserResponse) : HostState()
    data class HostDeleted(val hosts: ArrayList<Host>) : HostState()
    data class HostUpdated(val host: Host) : HostState()
    object ErrorFetchInstagram : HostState()
}