package com.silent.newpodcasts.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silent.core.data.model.youtube.SectionItem
import com.silent.core.data.podcast.Podcast
import com.silent.core.service.YoutubeService
import kotlinx.coroutines.*

class NewPodcastViewModel : ViewModel() {

    val podcast = Podcast()
    private val youtubeService = YoutubeService()

    sealed class NewPodcastState {
        data class UploadNewPodcastState(val podcast: Podcast) : NewPodcastState()
        data class NewPodCastRetrievedState(val podcast: Podcast) : NewPodcastState()
        data class NewPodCastChannelsRetrieved(val channels: ArrayList<Podcast>) : NewPodcastState()
        object NewPodcastError : NewPodcastState()
    }

    val newPodcastState = MutableLiveData<NewPodcastState>()

    fun getRelatedChannels(channelID: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val channelSectionListResponse = youtubeService.getChannelRelatedChannels(channelID)
            filterRelatedChannels(channelSectionListResponse.items)
        }
    }

    fun getChannel(channelID: String, podcastRetrieve: (Podcast) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val channelDetails = youtubeService.getChannelDetails(channelID)
            val channel = channelDetails.items[0]
            val dataPodcast = Podcast(
                youtubeID = channel.snippet.channelId,
                name = channel.snippet.title,
                iconURL = channel.snippet.thumbnails.medium
            )
            podcastRetrieve(dataPodcast)
        }
    }

    private suspend fun filterRelatedChannels(sectionItems: List<SectionItem>) {
        val podcastList = ArrayList<Podcast>()
            val relatedChannelsSection = sectionItems.find { it.snippet.type == "multiplechannels" }
            relatedChannelsSection?.let { item ->
                print(item)
                val relatedChannels = item.contentDetails["channels"] as List<String>
                GlobalScope.launch(Dispatchers.IO) {
                    relatedChannels.forEachIndexed { index, channelID ->
                        val channelDetails = youtubeService.getChannelDetails(channelID)
                        val channel = channelDetails.items[0]
                        val dataPodcast = Podcast(
                            youtubeID = channel.snippet.channelId,
                            name = channel.snippet.title,
                            iconURL = channel.snippet.thumbnails.medium
                        )
                        podcastList.add(dataPodcast)
                        if (index == relatedChannels.lastIndex) {
                            newPodcastState.postValue(NewPodcastState.NewPodCastChannelsRetrieved(podcastList))
                        }
                    }
                }
        }
    }
}