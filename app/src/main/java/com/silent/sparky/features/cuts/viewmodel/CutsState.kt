package com.silent.sparky.features.cuts.viewmodel

import com.silent.sparky.features.cuts.data.PodcastCutHeader

sealed class CutsState {
    data class CutsRetrieved(val cutHeader: ArrayList<PodcastCutHeader>) :
        CutsState()

    object CutsError : CutsState()
}
