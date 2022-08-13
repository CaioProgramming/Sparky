package com.silent.sparky.features.live.data

import com.silent.core.podcast.Podcast
import java.io.Serializable

data class LiveHeader(
    val podcast: Podcast,
    val title: String,
    val description: String,
    val videoID: String,
    val isLiveVideo: Boolean = false
) : Serializable