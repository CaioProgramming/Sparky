package com.silent.core.flow.data

data class FlowProfileResponse(val profile: FlowProfile)

data class FlowProfile(
    val username: String,
    val profile_picture: String,
    val created_at: String,
    val bio: String,
    val total_badges: Int = 0
)
