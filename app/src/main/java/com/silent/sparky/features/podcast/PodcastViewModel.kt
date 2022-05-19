package com.silent.sparky.features.podcast

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.instagram.InstagramService
import com.silent.core.podcast.Host
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.videos.Video
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.sparky.data.PodcastHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PodcastViewModel(application: Application) : BaseViewModel<Podcast>(application) {

    private val youtubeService = YoutubeService()
    private val instagramService = InstagramService()
    override val service = PodcastService()
    private val videoService = VideoService()
    private val videoMapper = VideoMapper()


    sealed class ChannelState {
        object ChannelFailedState : ChannelState()
        data class ChannelHostRetrieved(
            val host: Host
        ) : ChannelState()

        data class ChannelDataRetrieved(
            val podcast: Podcast,
            val headers: ArrayList<PodcastHeader>
        ) : ChannelState()
    }

    val channelState = MutableLiveData<ChannelState>()

    private fun getHeader(
        title: String,
        playlistId: String,
        videos: List<Video>,
        orientation: Int,
        highlightColor: String
    ) = PodcastHeader(
        title = title,
        playlistId = playlistId,
        videos = videos,
        orientation = orientation,
        highLightColor = highlightColor
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
                val uploads = videoService.query(
                    podcast.youtubeID,
                    "podcastId"
                ).success.data as ArrayList<Video>
                //val cuts = youtubeService.getPlaylistVideos(podcast.cuts)
                val uploadHeader = getHeader(
                    "Últimos episódios",
                    podcast.uploads,
                    uploads.sortedByDescending { it.publishedAt },
                    RecyclerView.HORIZONTAL,
                    podcast.highLightColor
                )
                val mappedCuts = ArrayList<Video>()
                /* cuts.items.forEach {
                     mappedCuts.add(videoMapper.mapVideoSnippet(it.snippet, podcastID))
                 }*/
                val cutsHeader = getHeader(
                    "Úlitmos cortes",
                    podcast.cuts,
                    mappedCuts,
                    RecyclerView.VERTICAL,
                    podcast.highLightColor
                )
                channelState.postValue(
                    ChannelState.ChannelDataRetrieved(
                        podcast,
                        arrayListOf(uploadHeader, cutsHeader)
                    )
                )
                //getHostsData(podcast.hosts)
            } catch (e: Exception) {
                e.printStackTrace()
                channelState.postValue(ChannelState.ChannelFailedState)
            }
        }
    }

}