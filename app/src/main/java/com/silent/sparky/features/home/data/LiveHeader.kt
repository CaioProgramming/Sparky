package com.silent.sparky.features.home.data

import com.silent.core.podcast.Podcast
import com.silent.core.videos.Video
import com.silent.core.youtube.SearchItem
import java.io.Serializable

data class LiveHeader(val podcast: Podcast, val title: String, val videoID: String) : Serializable