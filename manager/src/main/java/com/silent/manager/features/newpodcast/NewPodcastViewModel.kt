package com.silent.manager.features.newpodcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.instagram.InstagramService
import com.silent.core.podcast.Host
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
private const val FLOW_STUDIOS_ID = "UCmw6h7iv_A_nHA1nlnhkAAA"

class NewPodcastViewModel : BaseViewModel<Podcast>() {

    override val service = PodcastService()
    private val youtubeService = YoutubeService()
    private val instagramService = InstagramService()

    val newPodcastState = MutableLiveData<NewPodcastState>()
    val hostState = MutableLiveData<HostState>()

    var podcast = Podcast()

    fun updatePodcast(newPodcast: Podcast) {
        this.podcast = newPodcast
        newPodcastState.value = NewPodcastState.PodcastUpdated
    }

    fun updateHosts(host: Host) {
        this.podcast.hosts.add(host)
        hostState.value = HostState.HostUpdated(host)
    }

    fun deleteHost(host: Host) {
        podcast.hosts.remove(host)
        hostState.postValue(HostState.HostDeleted(podcast.hosts))
    }

    fun checkPodcast(newPodcast: Podcast) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val actualPodcasts = service.getAllData().success.data
                if (actualPodcasts.any {
                        it.youtubeID == newPodcast.youtubeID
                    }) {
                    newPodcastState.postValue(NewPodcastState.InvalidPodcast)
                } else {
                    newPodcastState.postValue(NewPodcastState.ValidPodcast(newPodcast))
                }
            } catch (e: Exception) {
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

    fun getRelatedCuts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val channelSections = youtubeService.getChannelSections(FLOW_STUDIOS_ID)
                filterRelatedChannels(channelSections.items, true)
            } catch (e: Exception) {
                e.printStackTrace()
                viewModelState.postValue(ViewModelBaseState.ErrorState(DataException(ErrorType.UNKNOWN)))
            }
        }
    }


    fun getInstagramData(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val instagramResponse = instagramService.getUserInfo(userName)
                hostState.postValue(HostState.HostInstagramRetrieve(instagramResponse.graphql.user))
            } catch (e: Exception) {
                e.printStackTrace()
                hostState.postValue(HostState.ErrorFetchInstagram)
            }
        }
    }

    private suspend fun filterRelatedChannels(
        sectionItem: List<SectionItem>,
        cuts: Boolean = false
    ) {
        val relatedChannelsSection = sectionItem.find { it.snippet.type == "multiplechannels" }
        val channels = relatedChannelsSection!!.contentDetails["channels"] as List<String>
        channels.forEach {
            val channel = youtubeService.getChannelDetails(it).items[0]
            val podcast = Podcast(
                youtubeID = channel.id,
                name = channel.snippet.title,
                iconURL = channel.snippet.thumbnails.high.url
            )
            if (!cuts) {
                newPodcastState.postValue(NewPodcastState.RelatedChannelRetrieved(podcast))
            } else {
                podcast.cuts = channel.contentDetails.relatedPlaylists.uploads
                newPodcastState.postValue(NewPodcastState.RelatedCutsRetrieved(podcast))

            }
        }
    }

}