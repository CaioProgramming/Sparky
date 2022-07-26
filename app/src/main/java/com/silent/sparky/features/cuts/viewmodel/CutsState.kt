package com.silent.sparky.features.cuts.viewmodel

import com.silent.core.videos.Video

sealed class CutsState {
    data class CutsRetrieved(val videos: ArrayList<Video>) :
        CutsState()

    object CutsError : CutsState()
}
