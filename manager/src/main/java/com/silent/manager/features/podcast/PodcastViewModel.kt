package com.silent.manager.features.podcast

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastMapper
import com.silent.core.podcast.PodcastService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PodcastViewModel(application: Application) : BaseViewModel<Podcast>(application) {


    override val service = PodcastService()
    private val youtubeService = YoutubeService()
    private val podcastMapper = PodcastMapper()

    fun updatePodcastData(podcast: Podcast) {
        viewModelScope.launch(Dispatchers.IO) {
            val channel = youtubeService.getChannelDetails(podcast.youtubeID).items.first()
            podcast.apply {
                youtubeID = channel.id
                iconURL = channel.snippet.thumbnails.high.url
                subscribe = channel.statistics.subscriberCount
                views = channel.statistics.viewCount
                uploads = channel.contentDetails.relatedPlaylists.uploads
            }
            editData(podcast)
        }
    }


}