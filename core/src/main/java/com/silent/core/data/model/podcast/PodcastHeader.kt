package com.silent.core.data.model.podcast

import androidx.recyclerview.widget.RecyclerView
import com.silent.core.data.podcast.Podcast
import com.silent.core.youtube.PlaylistResource

typealias podcastSections = ArrayList<PodcastHeader>
data class PodcastHeader(val title: String,
                         val programIcon: String? = null,
                         val videos: List<PlaylistResource>,
                         val playlistId: String,
                         val orientation: Int = RecyclerView.VERTICAL,
                         val podcast: Podcast? = null)
