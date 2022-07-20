package com.silent.core.podcast

import androidx.recyclerview.widget.RecyclerView
import com.silent.core.videos.Video
import java.io.Serializable

typealias programSections = ArrayList<PodcastHeader>

data class PodcastHeader(
    val title: String,
    val icon: String? = null,
    val highLightColor: String? = null,
    val videos: ArrayList<Video>? = null,
    val playlistId: String? = null,
    val channelURL: String? = null,
    val orientation: Int = RecyclerView.VERTICAL,
    val seeMore: Boolean = false,
    var referenceIndex: Int? = null,
    var subTitle: String? = null,
    val podcasts: List<Podcast>? = null,
    val type: HeaderType = HeaderType.VIDEOS
): Serializable

enum class HeaderType {
    VIDEOS, PODCASTS
}