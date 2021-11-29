package com.silent.sparky.features.home.data

import com.silent.core.podcast.Podcast
import com.silent.core.youtube.SearchItem
import java.io.Serializable

data class LiveHeader(val podcast: Podcast, val video: SearchItem) : Serializable