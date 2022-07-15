package com.silent.sparky.features.profile.viewmodel

import com.silent.core.flow.data.FlowProfile

sealed class ProfileState {

    data class FlowUserRetrieve(val flowProfile: FlowProfile) : ProfileState()
    object FlowUserError : ProfileState()

}
