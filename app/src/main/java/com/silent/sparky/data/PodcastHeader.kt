package com.silent.sparky.data

import androidx.recyclerview.widget.RecyclerView
import com.silent.core.youtube.PlaylistResource

typealias programSections = ArrayList<PodcastHeader>

data class PodcastHeader(
    val title: String,
    val icon: String? = null,
    val highLightColor: String? = null,
    val videos: List<PlaylistResource>,
    val playlistId: String,
    val channelURL: String? = null,
    val orientation: Int = RecyclerView.VERTICAL,
    val seeMore: Boolean = false
)
