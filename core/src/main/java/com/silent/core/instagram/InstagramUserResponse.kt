package com.silent.core.instagram


data class InstagramResponse(val graphQL: InstagramGraph)

data class InstagramGraph(val user: InstagramUserResponse)

data class InstagramUserResponse(
    val full_name: String,
    val profile_pic_url: String,
    val username: String
)