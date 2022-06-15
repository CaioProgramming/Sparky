package com.silent.sparky.features.cuts.viewmodel

import com.silent.core.videos.Video
import com.silent.core.youtube.PlaylistResource

sealed class CutsState {
    data class CutsRetrieved(val playlistID: String, val videos: ArrayList<Video>) :
        CutsState()

    object CutsError : CutsState()
}
