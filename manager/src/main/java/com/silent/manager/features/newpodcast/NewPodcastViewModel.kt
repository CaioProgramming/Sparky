package com.silent.manager.features.newpodcast

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.*
import com.silent.core.videos.CutService
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.SectionItem
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ErrorType
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.manager.features.newpodcast.fragments.youtube.PodcastsHeader
import com.silent.manager.states.HostState
import com.silent.manager.states.NewPodcastState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val VENUS_CHANNEL_ID = "UCTBhsXf_XRxk8w4rMj6WBOA"
private const val FLOW_SPORT_CLUB_ID = "UC5-aueB1RqpUc-EeAjtx9Lw"

class NewPodcastViewModel(
    application: Application,
    override val service: PodcastService,
    private val youtubeService: YoutubeService,
    private val podcastMapper: PodcastMapper,
    private val videoMapper: VideoMapper,
    private val videoService: VideoService,
    private val cutService: CutService
) : BaseViewModel<Podcast>(application) {

    val newPodcastState = MutableLiveData<NewPodcastState>()
    val hostState = MutableLiveData<HostState>()

    var podcast = Podcast()

    override fun saveData(data: Podcast) {
        viewModelScope.launch(Dispatchers.IO) {
            data.apply {
                hosts = ArrayList(this.hosts.filter { it.name != NEW_HOST })
            }
            updateEpisodesAndCuts(data)
            super.saveData(data)
        }

    }

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
                        val podcast = it as Podcast
                        podcast.youtubeID == newPodcast.youtubeID
                    }) {
                    newPodcastState.postValue(NewPodcastState.InvalidPodcast)
                } else {
                    podcast = newPodcast
                    newPodcastState.postValue(NewPodcastState.ValidPodcast(newPodcast))
                }
            } catch (e: Exception) {
                newPodcastState.postValue(NewPodcastState.ValidPodcast(newPodcast))
            }
        }
    }

    fun getRelatedChannels(filter: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val channelSections = youtubeService.getChannelSections(FLOW_SPORT_CLUB_ID)
                filterRelatedChannels(channelSections.items, filter)
            } catch (e: Exception) {
                e.printStackTrace()
                viewModelState.postValue(ViewModelBaseState.ErrorState(DataException(ErrorType.UNKNOWN)))
            }
        }
    }

    fun getRelatedCuts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val channelID = podcast.youtubeID
                val channelSections = youtubeService.getChannelSections(channelID)
                filterRelatedChannels(channelSections.items)
            } catch (e: Exception) {
                e.printStackTrace()
                viewModelState.postValue(ViewModelBaseState.ErrorState(DataException(ErrorType.UNKNOWN)))
            }
        }
    }

    private suspend fun filterRelatedChannels(
        sectionItem: List<SectionItem>,
        filter: String? = null
    ) {
        val podcastsList = service.getAllData().success.data as podcasts
        val podcastHeaders = ArrayList<PodcastsHeader>()
        val multipleChannelsList = if (filter != null) {
            sectionItem.filter { it.snippet.type == "multiplechannels" && it.snippet.title == filter }
        } else {
            sectionItem.filter { it.snippet.type == "multiplechannels" }
        }
        multipleChannelsList.forEachIndexed { index, sectionItem ->
            val channels = sectionItem.contentDetails["channels"] as List<String>
            val relatedPodcasts = ArrayList<Podcast>()
            channels.forEach { podcastID ->
                val channel = youtubeService.getChannelDetails(podcastID).items.first()
                val podcast = podcastMapper.mapChannelResponse(channel)
                if (!podcastsList.any { it.youtubeID == podcast.youtubeID }) relatedPodcasts.add(podcast)
            }
            podcastHeaders.add(PodcastsHeader(sectionItem.snippet.title, relatedPodcasts))
            if (index == multipleChannelsList.lastIndex) {
                if (podcast.id.isNotEmpty()) {
                    newPodcastState.postValue(NewPodcastState.RelatedCutsRetrieved(podcastHeaders))
                } else {
                    newPodcastState.postValue(
                        NewPodcastState.RelatedPodcastsRetrieved(
                            podcastHeaders
                        )
                    )
                }
            }
        }
    }

    private suspend fun updateEpisodesAndCuts(podcast: Podcast) {
        val uploads = youtubeService.getPlaylistVideos(podcast.uploads)
        val cuts = youtubeService.getPlaylistVideos(podcast.cuts)
        uploads.items.forEachIndexed { index, playlistResource ->
            val video = videoMapper.mapVideoSnippet(playlistResource.snippet, podcast.id)
            videoService.editData(video)
        }
        cuts.items.forEachIndexed { index, playlistResource ->
            val video = videoMapper.mapVideoSnippet(playlistResource.snippet, podcast.id)
            cutService.editData(video)
        }
    }

}