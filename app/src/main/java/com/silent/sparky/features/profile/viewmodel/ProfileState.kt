package com.silent.sparky.features.profile.viewmodel

import com.silent.core.flow.data.FlowProfile
import com.silent.core.stickers.response.Badge

sealed class ProfileState {

    data class FlowUserRetrieve(val flowProfile: FlowProfile) : ProfileState()
    object FlowUserError : ProfileState()

}

sealed class StickersState {
    object FetchingStickers: StickersState()
    object ErrorFetchingStickers: StickersState()
    data class StickersRetrieved(val badges: List<Badge>) : StickersState()

}
