package com.silent.sparky.features.home.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.podcast.podcasts
import com.silent.core.users.UsersService
import com.silent.core.videos.Video
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.sparky.data.PodcastHeader
import com.silent.sparky.features.home.data.LiveHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : BaseViewModel<Podcast>(application) {

    override val service = PodcastService()
    val videoService = VideoService()
    private val youtubeService = YoutubeService()
    private val userService = UsersService()
    val homeState = MutableLiveData<HomeState>()


    fun getHome() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                service.currentUser()?.let {
                    checkManager(it.uid)
                }
                val podcasts = service.getAllData().success.data as podcasts
                val sortedPodcasts = podcasts.sortedByDescending { it.subscribe }
                sortedPodcasts.forEach {
                    val uploadRequest = videoService.getHomeVideos(it.youtubeID)
                    print(uploadRequest)
                    val uploads = uploadRequest.success.data
                    val videos = uploads.sortedByDescending { v -> v.publishedAt }
                    val header = createHeader(it, videos.subList(0, 10), it.id)
                    homeState.postValue(HomeState.HomeChannelRetrieved(header))
                }
                //checkLives(ArrayList(podcasts))
            } catch (e: Exception) {
                e.printStackTrace()
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
        uploads: List<Video>,
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