package com.silent.sparky.features.home.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.*
import com.silent.core.preferences.PreferencesService
import com.silent.core.users.User
import com.silent.core.users.UsersService
import com.silent.core.utils.WARNING_PREFERENCE
import com.silent.core.videos.Video
import com.silent.core.videos.VideoService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.model.ViewModelBaseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val WARNING_HIDE_TIME = 5000L

class HomeViewModel(
    myApplication: Application,
    override val service: PodcastService,
    private val videoService: VideoService,
    private val usersService: UsersService,
    private val preferencesService: PreferencesService
) : BaseViewModel<Podcast>(myApplication) {


    val homeState = MutableLiveData<HomeState>()
    val userState = MutableLiveData<UserState>()
    val preferencesState = MutableLiveData<PreferencesState>()

    private fun createTopHeader(podcasts: podcasts, liveVideoId: String? = null): PodcastHeader {
        val topHeaderPodcasts = podcasts.toList().apply {
            map {
                it.isLive = liveVideoId == it.id
            }
        }.sortedByDescending { it.subscribe }
        return PodcastHeader(
            type = HeaderType.PODCASTS,
            showDivider = true,
            showTitle = false,
            podcasts = ArrayList(topHeaderPodcasts),
            orientation = RecyclerView.HORIZONTAL
        )
    }

    fun getHome(notificationLive: Video? = null) {
        if (getUser() == null) {
            viewModelState.postValue(ViewModelBaseState.RequireAuth)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val warningShown = preferencesService.getBooleanValue(WARNING_PREFERENCE)
                if (!warningShown) {
                    preferencesState.postValue(PreferencesState.WarningNotShowed)
                    return@launch
                }
                viewModelState.postValue(ViewModelBaseState.LoadingState)
                val homeHeaders = ArrayList<PodcastHeader>()
                val podcasts = (service.getAllData().success.data as podcasts)
                homeHeaders.add(createTopHeader(podcasts, notificationLive?.podcastId))
                val subscribed = podcasts.any { it.subscribers.contains(getUser()!!.uid) }
                if (!subscribed) {
                    preferencesState.postValue(PreferencesState.PreferencesNotSet)
                }
                val filteredPodcasts = if (subscribed) podcasts.filter {
                    it.subscribers.contains(getUser()!!.uid)
                } else podcasts
                val sortedPodcasts = filteredPodcasts.sortedByDescending { it.subscribe }
                sortedPodcasts.forEachIndexed { index, podcast ->
                    when (val uploadsData = videoService.getHomeVideos(podcast.id)) {
                        is ServiceResult.Error -> {}
                        is ServiceResult.Success -> {
                            val sortedVideos = (uploadsData.data as ArrayList<Video>)
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
                    viewModelState.postValue(ViewModelBaseState.LoadCompleteState)
                    if (index == filteredPodcasts.lastIndex) {
                        val remainingPodcasts =
                            podcasts.filter { !it.subscribers.contains(getUser()!!.uid) }
                        if (remainingPodcasts.isNotEmpty()) {
                            homeHeaders.add(
                                PodcastHeader(
                                    "Veja mais podcasts",
                                    "Conheça a família Flow!",
                                    type = HeaderType.PODCASTS,
                                    podcasts = remainingPodcasts,
                                    orientation = RecyclerView.HORIZONTAL
                                )
                            )
                        }
                    }
                    homeState.postValue(HomeState.HomeChannelsRetrieved(homeHeaders))
                }
                checkLives(sortedPodcasts)
                checkManager()

            } catch (e: Exception) {
                e.printStackTrace()
                homeState.postValue(HomeState.HomeError)
            }
        }
    }

    fun searchPodcastAndEpisodes(query: String) {
        if (query.isBlank()) {
            getHome()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            viewModelState.postValue(ViewModelBaseState.LoadingState)
            val headers = ArrayList<PodcastHeader>()
            val podcastsRequest = service.getAllData()
            when (podcastsRequest) {
                is ServiceResult.Error -> sendErrorState(podcastsRequest.error.errorException)
                is ServiceResult.Success -> {
                    val podcasts = podcastsRequest.success.data as ArrayList<Podcast>
                    val queryPodcasts = podcasts.filter { podcast ->
                        podcast.name.contains(query, true) || podcast.hosts.any { host ->
                            host.name.contains(query, true)
                        }
                    }.sortedByDescending { it.subscribe }
                    if (queryPodcasts.isNotEmpty()) {
                        headers.add(
                            PodcastHeader(
                                "Podcasts encontrados",
                                orientation = RecyclerView.HORIZONTAL,
                                seeMore = false,
                                type = HeaderType.PODCASTS,
                                podcasts = queryPodcasts
                            )
                        )
                    }
                    podcasts.forEachIndexed { index, podcast ->
                        when (val videoRequest = videoService.getPodcastVideos(podcast.id)) {
                            is ServiceResult.Success -> {
                                val videos = videoRequest.success.data as ArrayList<Video>
                                val queryVideos = ArrayList(videos.filter {
                                    it.title.contains(
                                        query,
                                        true
                                    ) || it.description.contains(query, true)
                                }.sortedByDescending { it.publishedAt })
                                val header = createHeader(podcast, queryVideos, podcast.uploads)
                                headers.add(header)
                            }
                            else -> {}
                        }

                        if (index == podcasts.lastIndex) {
                            val queryHeaders = headers.filter { header ->
                                header.title?.contains(
                                    query,
                                    true
                                ) == true || header.videos?.isNotEmpty() == true || header.podcasts?.isNotEmpty() == true
                            }
                            homeState.postValue(HomeState.HomeSearchRetrieved(ArrayList(queryHeaders)))
                        }
                    }
                }
            }

        }


    }

    private fun checkLives(podcasts: List<Podcast>) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val lives = ArrayList<Podcast>()
                homeState.postValue(HomeState.HomeFetched)
            }
        } catch (e: Exception) {
            homeState.postValue(HomeState.HomeLiveError)
        }
    }

    private fun checkManager() {
        viewModelScope.launch(Dispatchers.IO) {
            service.currentUser()?.let {
                try {
                    when (val userRequest = usersService.getSingleData(it.uid)) {
                        is ServiceResult.Error -> homeState.postValue(HomeState.InvalidManager)
                        is ServiceResult.Success -> {
                            val user = userRequest.data as User
                            if (user.admin) {
                                homeState.postValue(HomeState.ValidManager)
                            }
                            if (user.notifications.any { notification -> !notification.open }) {
                                userState.postValue(UserState.NewNotificationsState)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    homeState.postValue(HomeState.InvalidManager)
                }
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
            subTitle = "Últimos episódios do ${podcast.name}",
            icon = podcast.iconURL,
            channelURL = podcast.youtubeID,
            videos = ArrayList(uploads),
            playlistId = playlistID,
            orientation = RecyclerView.HORIZONTAL,
            seeMore = true,
            podcast = podcast,
            type = HeaderType.VIDEOS
        )

    }

    fun updateWarning() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(WARNING_HIDE_TIME)
            preferencesService.editPreference(WARNING_PREFERENCE, true)
            preferencesState.postValue(PreferencesState.PreferencesDone)
            getHome()
        }
    }
}