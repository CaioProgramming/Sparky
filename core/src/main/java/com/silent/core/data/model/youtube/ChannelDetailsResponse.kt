package com.silent.core.twitch

data class ChannelDetailsResponse(
    val kind: String,
    val etag: String,
    val id: String,
    val items: List<ChannelResource>
)

data class ChannelResource(val snippet: ChannelDetails,val contentDetails: ChannelContent, val brandingSettings: BrandingSettings)

data class ChannelContent(val relatedPlaylists: ChannelPlaylists)

data class ChannelPlaylists(val likes: String, val favorites: String, val uploads: String)

data class BrandingSettings(val image: BrandingImage)

data class BrandingImage(val bannerExternalUrl: String)

data class ChannelDetails(
    val type: String,
    val channelId: String,
    val title: String,
    val description: String,
    val position: Int,
    val thumbnails: Thumbnails
)

data class Thumbnails(val high: ThumbSettings)

data class ThumbSettings(val url: String, val width: Int, val height: Int)