package com.silent.sparky.features.home.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.podcast.podcasts
import com.silent.core.preferences.PreferencesService
import com.silent.core.users.User
import com.silent.core.users.UsersService
import com.silent.core.utils.PODCASTS_PREFERENCES
import com.silent.core.videos.Video
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.sparky.features.home.data.HeaderType
import com.silent.sparky.features.home.data.PodcastHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application,
    override val service: PodcastService,
    private val videoService: VideoService,
    private val youtubeService: YoutubeService,
    private val usersService: UsersService,
    private val videoMapper: VideoMapper,
    private val preferencesService: PreferencesService
) : BaseViewModel<Podcast>(application) {


    val homeState = MutableLiveData<HomeState>()


    fun getHome() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val podcastFilter = ArrayList<String>()
                val preferencesPodcasts = preferencesService.getStringSetValue(PODCASTS_PREFERENCES)
                if (preferencesPodcasts.isNullOrEmpty()) {
                    homeState.postValue(HomeState.PreferencesNotSet)
                } else {
                    podcastFilter.addAll(preferencesPodcasts)
                }
                val podcasts = service.getAllData().success.data as podcasts
                val filteredPodcasts = podcasts.filter { podcastFilter.contains(it.id) }
                val sortedPodcasts = filteredPodcasts.sortedByDescending { it.subscribe }
                val homeHeaders = ArrayList<PodcastHeader>()
                sortedPodcasts.forEachIndexed { index, podcast ->
                    val uploadsData = videoService.query(podcast.id, "podcastId")
                    when (uploadsData) {
                        is ServiceResult.Error -> {
                            Log.e(
                                javaClass.simpleName,
                                "Video Query for ${podcast.name}(${podcast.youtubeID}) not found"
                            )
                        }
                        is ServiceResult.Success -> {
                            val sortedVideos = (uploadsData.data as ArrayList<Video>).sortedByDescending { it.publishedAt }
                            sortedVideos.map { it.podcast = podcast }
                            homeHeaders.add(
                                createHeader(
                                    podcast,
                                    sortedVideos.subList(0, 10),
                                    podcast.id
                                )
                            )
                        }
                    }
                    if (index == filteredPodcasts.lastIndex) {
                        val remainingPodcasts = podcasts.filter { !podcastFilter.contains(it.id) }
                        homeHeaders.add(PodcastHeader("Veja mais podcasts", type = HeaderType.PODCASTS, podcasts = remainingPodcasts, orientation = RecyclerView.HORIZONTAL))
                        homeState.postValue(HomeState.HomeChannelsRetrieved(homeHeaders))
                    }
                }
                checkLives(sortedPodcasts)
                if (service.currentUser() == null) {
                    sendErrorState(DataException.AUTH)
                } else {
                    service.currentUser()?.let {
                        checkManager(it.uid)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                //homeState.postValue(HomeState.HomeError)
            }
        }
    }

    private fun checkLives(podcasts: List<Podcast>) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val lives = ArrayList<Podcast>()
                podcasts.forEachIndexed { index, it ->
                    if (it.weeklyGuests.any { guest -> guest.isComingToday() && guest.isLiveHour() }) {
                        val searchLive = youtubeService.getChannelLiveStatus(it.youtubeID)
                        if (searchLive.items.isNotEmpty()) {
                            val liveItem = searchLive.items.first()
                            it.liveVideo = videoMapper.mapLiveSnippet(
                                liveItem.id.videoId,
                                liveItem.snippet,
                                it.id
                            )
                            lives.add(it)
                        }
                    }
                    if (index == podcasts.lastIndex && lives.isNotEmpty()) {
                        homeState.postValue(HomeState.HomeLivesRetrieved(lives))
                    }
                }
                homeState.postValue(HomeState.HomeFetched)
            }


        } catch (e: Exception) {
            homeState.postValue(HomeState.HomeLiveError)
        }
    }

    suspend fun checkManager(uid: String) {
        try {
            when(val userRequest = usersService.getSingleData(uid)) {
                is ServiceResult.Error -> homeState.postValue(HomeState.InvalidManager)
                is ServiceResult.Success -> {
                    val user = userRequest.data as User
                    if (user.admin) {
                        homeState.postValue(HomeState.ValidManager)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            homeState.postValue(HomeState.InvalidManager)
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
            videos = ArrayList(uploads),
            playlistId = playlistID,
            orientation = RecyclerView.HORIZONTAL,
            seeMore = true
        )

    }
}