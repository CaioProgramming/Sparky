package com.silent.core.flow.data

data class FlowProfileResponse(val profile: FlowProfile)

data class FlowProfile(
    val username: String,
    val selected_badges: List<FlowBadge> = emptyList(),
    val total_badges: Int = 0
)

data class FlowBadge(
    val description: String,
    val name: String,
    val src: String,
    val updated_at: String
)