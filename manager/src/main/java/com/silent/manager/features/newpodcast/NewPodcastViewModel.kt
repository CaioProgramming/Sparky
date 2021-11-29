package com.silent.manager.features.newpodcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.instagram.InstagramService
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.youtube.SectionItem
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ErrorType
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.manager.states.HostState
import com.silent.manager.states.NewPodcastState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val VENUS_CHANNEL_ID = "UCTBhsXf_XRxk8w4rMj6WBOA"

class NewPodcastViewModel : BaseViewModel<Podcast>() {

    override val service = PodcastService()
    private val youtubeService = YoutubeService()
    private val instagramService = InstagramService()

    val newPodcastState = MutableLiveData<NewPodcastState>()
    val hostState = MutableLiveData<HostState>()

    private var podcast = Podcast()

    fun updatePodcast(newPodcast: Podcast) {
        this.podcast = newPodcast
        newPodcastState.value = NewPodcastState.PodcastUpdated
    }

    fun checkPodcast(newPodcast: Podcast) {
        viewModelScope.launch(Dispatchers.IO) {
            val actualPodcasts = service.getAllData().success.data
            if (actualPodcasts.any {
                    it.youtubeID == newPodcast.youtubeID
                }) {
                newPodcastState.postValue(NewPodcastState.InvalidPodcast)
            } else {
                newPodcastState.postValue(NewPodcastState.ValidPodcast(newPodcast))
            }
        }
    }


    fun getRelatedChannels() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val channelSections = youtubeService.getChannelSections(VENUS_CHANNEL_ID)
                filterRelatedChannels(channelSections.items)
            } catch (e: Exception) {
                e.printStackTrace()
                viewModelState.postValue(ViewModelBaseState.ErrorState(DataException(ErrorType.UNKNOWN)))
            }
        }
    }

    fun searchChannel(channel: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val channels = youtubeService.getChannelDetailsForUserName(channel)
                val podcasts = ArrayList<Podcast>()
                channels.items.forEach { channel ->
                    val podcast = Podcast(
                        youtubeID = channel.id,
                        iconURL = channel.snippet.thumbnails.high.url,
                        name = channel.snippet.title
                    )
                    podcasts.add(podcast)
                }
                newPodcastState.postValue(NewPodcastState.ChannelSearchRetrieved(podcasts))
            } catch (e: Exception) {
                viewModelState.postValue(ViewModelBaseState.ErrorState(DataException()))
            }
        }
    }

    fun getInstagramData(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val instagramUser = instagramService.getUserInfo(userName)
            hostState.postValue(HostState.HostInstagramRetrieve(instagramUser.graphQL.user))
        }
    }

    private suspend fun filterRelatedChannels(sectionItem: List<SectionItem>) {
        val relatedChannelsSection = sectionItem.find { it.snippet.type == "multiplechannels" }
        val channels = relatedChannelsSection!!.contentDetails["channels"] as List<String>
        channels.forEach {
            val channel = youtubeService.getChannelDetails(it).items[0]
            val podcast = Podcast(
                youtubeID = channel.id,
                name = channel.snippet.title,
                iconURL = channel.snippet.thumbnails.high.url
            )
            newPodcastState.postValue(NewPodcastState.RelatedChannelRetrieved(podcast))
        }
    }

}