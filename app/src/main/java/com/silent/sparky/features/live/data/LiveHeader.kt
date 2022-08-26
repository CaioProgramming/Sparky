package com.silent.sparky.features.live.data

import com.silent.core.podcast.Podcast
import java.io.Serializable

data class LiveHeader(
    val podcast: Podcast,
    var title: String,
    var description: String,
    var videoID: String,
    val type: VideoMedia
) : Serializable {

    val isLiveVideo = type == VideoMedia.LIVE

}

enum class VideoMedia {
    LIVE, EPISODE, CUT
}