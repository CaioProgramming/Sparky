package com.silent.sparky.features.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.users.UsersService
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
    private val userService = UsersService()
    val homeState = MutableLiveData<HomeState>()


    fun getHome() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                service.currentUser?.let {
                    checkManager(it.uid)
                }
                val podcasts = service.getAllData().success.data.sortedByDescending { it.subscribe }
                podcasts.forEach {
                    val uploads = youtubeService.getPlaylistVideos(it.uploads).items
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

    private fun checkManager(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userService.getSingleData(uid).success.data
                homeState.postValue(HomeState.ValidManager)
            } catch (e: Exception) {
                e.printStackTrace()
                homeState.postValue(HomeState.InvalidManager)
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