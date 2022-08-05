package com.silent.sparky.features.home.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.*
import com.silent.core.preferences.PreferencesService
import com.silent.core.users.User
import com.silent.core.users.UsersService
import com.silent.core.utils.PODCASTS_PREFERENCES
import com.silent.core.utils.WARNING_PREFERENCE
import com.silent.core.videos.Video
import com.silent.core.videos.VideoService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.model.ViewModelBaseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val FETCH_DELAY_TIME = 500L
private const val WARNING_HIDE_TIME = 5000L
private const val SHOW_PODCAST_PREFERENCE_DELAY = 2000L

class HomeViewModel(
    myApplication: Application,
    override val service: PodcastService,
    private val videoService: VideoService,
    private val usersService: UsersService,
    private val preferencesService: PreferencesService
) : BaseViewModel<Podcast>(myApplication) {


    val homeState = MutableLiveData<HomeState>()
    val preferencesState = MutableLiveData<PreferencesState>()

    fun getHome() {
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
                val podcasts = service.getAllData().success.data
                updateViewState(ViewModelBaseState.DataListRetrievedState(podcasts))
                delay(FETCH_DELAY_TIME)
                val podcastFilter = ArrayList<String>()
                val preferencesPodcasts = preferencesService.getStringSetValue(PODCASTS_PREFERENCES)
                if (preferencesPodcasts.isNullOrEmpty()) {
                    preferencesState.postValue(PreferencesState.PreferencesNotSet)
                } else {
                    podcastFilter.addAll(preferencesPodcasts)
                }
                val filteredPodcasts =
                    (podcasts as podcasts).filter { podcastFilter.contains(it.id) }
                val sortedPodcasts = filteredPodcasts.sortedByDescending { it.subscribe }
                val homeHeaders = ArrayList<PodcastHeader>()
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
                    if (index == filteredPodcasts.lastIndex) {
                        val remainingPodcasts = podcasts.filter { !podcastFilter.contains(it.id) }
                        if (remainingPodcasts.isNotEmpty()) {
                            homeHeaders.add(
                                PodcastHeader(
                                    "Veja mais podcasts",
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
            homeState.postValue(HomeState.LoadingSearch)
            val headers = ArrayList<PodcastHeader>()
            val podcasts = service.getAllData().success.data as ArrayList<Podcast>
            val queryPodcasts = podcasts.filter { podcast ->
                podcast.name.contains(query, true) || podcast.hosts.any { host ->
                    host.name.contains(query, true)
                }
            }
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
                        })
                        val header = createHeader(podcast, queryVideos, podcast.uploads)
                        headers.add(header)
                    }
                    else -> {}
                }

                if (index == podcasts.lastIndex) {
                    val queryHeaders = headers.filter { header ->
                        header.title.contains(
                            query,
                            true
                        ) || header.videos?.isNotEmpty() == true || header.podcasts?.isNotEmpty() == true
                    }
                    homeState.postValue(HomeState.HomeSearchRetrieved(ArrayList(queryHeaders)))
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
            podcast = podcast
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