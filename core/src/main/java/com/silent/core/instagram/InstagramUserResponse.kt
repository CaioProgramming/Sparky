package com.silent.core.instagram

import com.google.gson.annotations.SerializedName


data class InstagramWebResponse(
    @SerializedName("entry_data")
    val sharedData: EntryData
)

data class EntryData(
    @SerializedName("ProfilePage")
    val profilePage: List<InstagramResponse>
)

data class InstagramResponse(val graphql: InstagramGraph)

data class InstagramGraph(val user: InstagramUserResponse)

data class InstagramUserResponse(
    val full_name: String,
    val profile_pic_url: String,
    val username: String
)