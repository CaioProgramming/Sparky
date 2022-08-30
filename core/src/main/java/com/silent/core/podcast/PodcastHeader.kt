package com.silent.core.podcast

import androidx.recyclerview.widget.RecyclerView
import com.silent.core.videos.Video
import java.io.Serializable

typealias programSections = ArrayList<PodcastHeader>

data class PodcastHeader(
    val title: String? = null,
    var subTitle: String? = null,
    val icon: String? = null,
    val orientation: Int = RecyclerView.VERTICAL,
    val seeMore: Boolean = false,
    val showDivider: Boolean = false,
    val showTitle: Boolean = true,
    val highLightColor: String? = null,
    val type: HeaderType = HeaderType.VIDEOS,
    val videos: ArrayList<Video>? = null,
    val playlistId: String? = null,
    val channelURL: String? = null,
    var referenceIndex: Int? = null,
    val podcasts: List<Podcast>? = null,
    val podcast: Podcast? = null,

    ): Serializable

enum class HeaderType {
    VIDEOS, PODCASTS, CUTS
}