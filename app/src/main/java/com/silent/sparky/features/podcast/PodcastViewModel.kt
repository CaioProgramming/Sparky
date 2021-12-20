package com.silent.sparky.features.podcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.instagram.InstagramService
import com.silent.core.podcast.Host
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.youtube.PlaylistResource
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.sparky.data.PodcastHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PodcastViewModel : BaseViewModel<Podcast>() {

    private val youtubeService = YoutubeService()
    private val instagramService = InstagramService()
    override val service = PodcastService()


    sealed class ChannelState {
        object ChannelFailedState : ChannelState()
        data class ChannelHostRetrieved(
            val host: Host
        ) : ChannelState()

        data class ChannelDataRetrieved(
            val podcast: Podcast,
            val uploads: PodcastHeader,
            val cuts: PodcastHeader
        ) : ChannelState()
    }

    val channelState = MutableLiveData<ChannelState>()

    private fun getHeader(
        title: String,
        playlistId: String,
        videos: List<PlaylistResource>,
        orientation: Int
    ) = PodcastHeader(
        title = title,
        playlistId = playlistId,
        videos = videos,
        orientation = orientation
    )

    fun getHostsData(hosts: List<Host>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                hosts.forEach {
                    //val instaUser = instagramService.getUserInfo(it.user)
                    /*it.apply {
                        profilePic = instaUser.graphql.user.profile_pic_url
                        name = instaUser.graphql.user.full_name
                        user = instaUser.graphql.user.username
                    }*/
                    channelState.postValue(ChannelState.ChannelHostRetrieved(it))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun getChannelData(podcastID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val podcast = service.getSingleData(podcastID).success.data as Podcast
                val uploads = youtubeService.getPlaylistVideos(podcast.uploads)
                val cuts = youtubeService.getPlaylistVideos(podcast.cuts)
                val uploadHeader = getHeader(
                    "Últimos episódios",
                    podcast.uploads,
                    uploads.items,
                    RecyclerView.HORIZONTAL
                )
                val cutsHeader = getHeader(
                    "Úlitmos cortes",
                    podcast.cuts,
                    cuts.items,
                    RecyclerView.VERTICAL
                )
                channelState.postValue(
                    ChannelState.ChannelDataRetrieved(
                        podcast,
                        uploadHeader,
                        cutsHeader
                    )
                )
                getHostsData(podcast.hosts)
            } catch (e: Exception) {
                e.printStackTrace()
                channelState.postValue(ChannelState.ChannelFailedState)
            }
        }
    }

}