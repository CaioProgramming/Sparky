package com.silent.sparky.features.podcast.data

import androidx.recyclerview.widget.RecyclerView
import com.silent.core.youtube.PlaylistResource

typealias programSections = ArrayList<PodcastHeader>

data class PodcastHeader(
    val title: String,
    val videos: List<PlaylistResource>,
    val playlistId: String,
    val orientation: Int = RecyclerView.VERTICAL
)
