package com.silent.sparky.features.live.data

import com.silent.core.podcast.Podcast
import com.silent.core.videos.Video
import java.io.Serializable

data class LiveHeader(
    val podcast: Podcast,
    var video: Video,
    val type: VideoMedia
) : Serializable {

    val isLiveVideo = type == VideoMedia.LIVE

}

enum class VideoMedia {
    LIVE, EPISODE, CUT
}