package com.silent.sparky.features.live

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.*
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
    private val podcastService: PodcastService,
    application: Application
) : BaseViewModel<Video>(application) {
    sealed class LiveState {
        data class RelatedVideosRetrieved(val headers: List<PodcastHeader>) : LiveState()
    }

    sealed class VideoTitleState {
        data class UpdateTitleStyle(val newTitle: String) : VideoTitleState()

    }

    private fun doNothing() = Unit

    val liveState = MutableLiveData<LiveState>()
    val videoTitleState = MutableLiveData<VideoTitleState>()

    private fun getRelatedYoutubeEpId(description: String): String? {
        return try {
            val descriptionFormatting =
                description.substring(description.indexOf("https://youtu.be/"))
            //val descriptionYoutubeId = descriptionFormatting.subSequence(0, descriptionFormatting.indexOf(" "))
            val videoId = descriptionFormatting.toString().replace("https://youtu.be/", "")
            videoId
        } catch (e: Exception) {
            null
        }
    }


    fun getRelatedVideos(video: Video, podcast: Podcast, media: VideoMedia) {
        viewModelScope.launch(Dispatchers.IO) {
            val title = when (media) {
                VideoMedia.LIVE,
                VideoMedia.EPISODE -> "Mais do ${podcast.name}"
                VideoMedia.CUT -> "Mais cortes do ${podcast.name}"
            }
            val headers = ArrayList<PodcastHeader>()
            if (video.title.contains("@", true)) {
                val podcastsRequest = podcastService.getAllData()
                if (podcastsRequest.isSuccess) {
                    val podcasts = podcastsRequest.success.data as podcasts
                    val titleFiltered = podcasts.filter {
                        video.title.contains(
                            it.name,
                            true
                        ) && it.id != podcast.id
                    }
                    if (titleFiltered.isNotEmpty()) {
                        val header = PodcastHeader(
                            "Podcasts nesse episódio",
                            orientation = RecyclerView.HORIZONTAL,
                            seeMore = false,
                            type = HeaderType.PODCASTS,
                            podcasts = titleFiltered,
                            showDivider = true,
                        )
                        headers.add(header)
                        titleFiltered.forEach {
                            val formattedTitle = video.title.lowercase().replace(
                                it.name.lowercase(),
                                "<font color= '${it.highLightColor}'>${it.name}</font>"
                            )
                            videoTitleState.postValue(
                                VideoTitleState.UpdateTitleStyle(
                                    formattedTitle.uppercase()
                                )
                            )
                        }
                    }
                }
            }
            val videosRequest = when (media) {
                VideoMedia.LIVE,
                VideoMedia.EPISODE -> service.getPodcastVideos(podcast.id)
                VideoMedia.CUT -> cutService.getPodcastCuts(podcast.id)
            }
            when (videosRequest) {
                is ServiceResult.Error -> doNothing()
                is ServiceResult.Success -> {
                    if (media == VideoMedia.CUT) {
                        getRelatedYoutubeEpId(video.description)?.let { videoId ->
                            val podcastVideosRequest = service.getPodcastVideos(podcast.id)
                            if (podcastVideosRequest.isSuccess) {
                                val video =
                                    (podcastVideosRequest.success.data as ArrayList<Video>).find {
                                        videoId.contains(
                                            it.youtubeID,
                                            true
                                        )
                                    }
                                video?.let {
                                    val header = PodcastHeader(
                                        "Episódio desse corte",
                                        subTitle = "Veja o episódio completo.",
                                        podcast = podcast,
                                        orientation = RecyclerView.HORIZONTAL,
                                        seeMore = false,
                                        highLightColor = podcast.highLightColor,
                                        videos = ArrayList(listOf(it)),
                                        playlistId = podcast.id
                                    )
                                    headers.add(header)
                                }

                            }
                        }
                    } else if (media == VideoMedia.EPISODE) {
                        val podcastCutsRequest = cutService.getPodcastCuts(podcast.id)
                        if (podcastCutsRequest.isSuccess) {
                            val cuts =
                                (podcastCutsRequest.success.data as ArrayList<Video>).filter {
                                    it.description.contains(video.youtubeID)
                                }
                            val header = PodcastHeader(
                                "Cortes deste episódio",
                                subTitle = "Veja os cortes deste episódio.",
                                podcast = podcast,
                                orientation = RecyclerView.HORIZONTAL,
                                seeMore = false,
                                highLightColor = podcast.highLightColor,
                                videos = ArrayList(cuts),
                                playlistId = podcast.id
                            )
                            headers.add(header)
                        }
                    }
                    val videos =
                        ArrayList((videosRequest.data as ArrayList<Video>).filter { it.id != video.id }
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
                    headers.add((header))
                    liveState.postValue(LiveState.RelatedVideosRetrieved(headers))
                }
            }
        }
    }

    fun formatCoHostName(videoTitle: String, highlightColor: String) {
        var newTitle = videoTitle
        if (videoTitle.contains("[") && videoTitle.contains("]")) {
            val formattedTitle = newTitle.replace("[", "<font color= '$highlightColor'>")
                .replace("]", "</font>")
            newTitle = formattedTitle
            videoTitleState.postValue(VideoTitleState.UpdateTitleStyle(newTitle))
        }
    }

}