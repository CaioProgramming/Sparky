package com.silent.core.twitch

data class ChannelDetailsResponse(
    val kind: String,
    val etag: String,
    val id: String,
    val items: List<ChannelResource>
)

data class ChannelResource(val snippet: ChannelDetails,val contentDetails: ChannelContent, )

data class ChannelContent(val relatedPlaylists: ChannelPlaylists)

data class ChannelPlaylists(val likes: String, val favorites: String, val uploads: String)

data class ChannelDetails(
    val type: String,
    val channelId: String,
    val title: String,
    val description: String,
    val position: Int
)