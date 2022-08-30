package com.silent.sparky.features.cuts.viewmodel

import com.silent.core.podcast.podcasts
import com.silent.core.videos.Video

sealed class CutsState {
    data class CutsRetrieved(val videos: ArrayList<Video>, val podcasts: podcasts) : CutsState()
    object CutsError : CutsState()
}
