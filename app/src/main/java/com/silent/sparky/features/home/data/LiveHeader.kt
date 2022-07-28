package com.silent.sparky.features.home.data

import com.silent.core.podcast.Podcast
import java.io.Serializable

data class LiveHeader(val podcast: Podcast, val title: String, val videoID: String) : Serializable