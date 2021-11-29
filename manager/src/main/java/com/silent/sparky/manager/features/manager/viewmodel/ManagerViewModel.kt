package com.silent.sparky.manager.features.manager.viewmodel

import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.ilustriscore.core.model.BaseViewModel

class ManagerViewModel : BaseViewModel<Podcast>() {
    override val service = PodcastService()
}