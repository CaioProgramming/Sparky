package com.silent.sparky.features.live

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastHeader
import com.silent.core.videos.CutService
import com.silent.core.videos.Video
import com.silent.core.videos.VideoService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.sparky.features.live.data.VideoMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveViewModel(
    override val service: VideoService,
    private val cutService: CutService,
    application: Application
) : BaseViewModel<Video>(application) {
    sealed class LiveState {
        data class RelatedVideosRetrieved(val header: PodcastHeader) : LiveState()
    }

    private fun doNothing() = Unit

    val liveState = MutableLiveData<LiveState>()

    fun getRelatedVideos(videoId: String, podcast: Podcast, media: VideoMedia) {
        viewModelScope.launch(Dispatchers.IO) {
            val title = when (media) {
                VideoMedia.LIVE,
                VideoMedia.EPISODE -> "Mais do ${podcast.name}"
                VideoMedia.CUT -> "Mais cortes do ${podcast.name}"
            }
            val videosRequest = when (media) {
                VideoMedia.LIVE,
                VideoMedia.EPISODE -> service.getPodcastVideos(podcast.id)
                VideoMedia.CUT -> cutService.getPodcastCuts(podcast.id)
            }
            when (videosRequest) {
                is ServiceResult.Error -> doNothing()
                is ServiceResult.Success -> {
                    val videos =
                        ArrayList((videosRequest.data as ArrayList<Video>).filter { it.id != videoId }
                            .sortedByDescending { it.publishedAt }.subList(0, 20))
                    val header = PodcastHeader(
                        title,
                        podcast = podcast,
                        orientation = RecyclerView.VERTICAL,
                        seeMore = true,
                        highLightColor = podcast.highLightColor,
                        videos = videos,
                        playlistId = podcast.id,
                    )
                    liveState.postValue(LiveState.RelatedVideosRetrieved(header))
                }
            }
        }


    }

}