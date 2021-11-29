package com.silent.manager.states

import com.silent.core.instagram.InstagramUserResponse
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts

sealed class NewPodcastState {
    data class RelatedChannelRetrieved(val podcast: Podcast) : NewPodcastState()
    data class ChannelSearchRetrieved(val podcast: podcasts) : NewPodcastState()
    data class ValidPodcast(val podcast: Podcast) : NewPodcastState()
    object PodcastUpdated : NewPodcastState()
    object InvalidPodcast : NewPodcastState()
}

sealed class HostState {
    data class HostInstagramRetrieve(val instagramUserResponse: InstagramUserResponse) : HostState()
}