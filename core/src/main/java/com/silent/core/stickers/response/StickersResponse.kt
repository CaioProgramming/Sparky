package com.silent.core.stickers.response

data class StickersResponse(
    val badges: List<List<Badge>>,
    val status: Status
)