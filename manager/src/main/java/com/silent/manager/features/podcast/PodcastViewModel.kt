package com.silent.manager.features.podcast

import android.app.Application
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.ilustriscore.core.model.BaseViewModel

class PodcastViewModel(application: Application) : BaseViewModel<Podcast>(application) {

    override val service = PodcastService()

}