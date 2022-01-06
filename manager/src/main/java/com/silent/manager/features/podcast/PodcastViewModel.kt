package com.silent.manager.features.podcast

import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.ilustriscore.core.model.BaseViewModel

class PodcastViewModel : BaseViewModel<Podcast>() {

    override val service = PodcastService()

}