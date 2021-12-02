package com.silent.sparky.features.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.youtube.PlaylistResource
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.sparky.data.PodcastHeader
import com.silent.sparky.features.home.data.LiveHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel<Podcast>() {

    override val service = PodcastService()
    private val youtubeService = YoutubeService()
    val homeState = MutableLiveData<HomeState>()


    fun getHome() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val podcasts = service.getAllData().success.data.reversed()
                podcasts.forEach {
                    val podcastData = youtubeService.getChannelDetails(it.youtubeID).items[0]
                    val uploads =
                        youtubeService.getChannelUploads(podcastData.contentDetails.relatedPlaylists.uploads).items
                    it.name = podcastData.snippet.title
                    it.iconURL = podcastData.snippet.thumbnails.high.url
                    val header = createHeader(it, uploads, it.id)
                    homeState.postValue(HomeState.HomeChannelRetrieved(header))
                }
                checkLives(ArrayList(podcasts))
            } catch (e: Exception) {
                homeState.postValue(HomeState.HomeError)
            }

        }
    }

    private fun checkLives(podcasts: ArrayList<Podcast>) {
        viewModelScope.launch {
            try {
                val livePodcasts = ArrayList<LiveHeader>()
                podcasts.forEach {
                    val searchLive = youtubeService.getChannelLiveStatus(it.youtubeID)
                    if (searchLive.items.isNotEmpty()) {
                        livePodcasts.add(LiveHeader(it, searchLive.items[0]))
                    }
                }
                homeState.postValue(HomeState.HomeLivesRetrieved(livePodcasts))
            } catch (e: Exception) {
                homeState.postValue(HomeState.HomeLiveError)
            }
        }
    }

    private fun createHeader(
        podcast: Podcast,
        uploads: List<PlaylistResource>,
        playlistID: String
    ): PodcastHeader {
        return PodcastHeader(
            title = podcast.name,
            icon = podcast.iconURL,
            channelURL = podcast.youtubeID,
            videos = uploads,
            playlistId = playlistID,
            orientation = RecyclerView.HORIZONTAL
        )

    }
}