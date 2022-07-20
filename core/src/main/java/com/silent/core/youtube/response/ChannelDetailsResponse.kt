package com.silent.core.youtube

data class ChannelDetailsResponse(
    val kind: String,
    val etag: String,
    val id: String,
    val items: List<ChannelResource>
)

data class ChannelResource(
    val id: String,
    val snippet: ChannelDetails,
    val contentDetails: ChannelContent,
    val brandingSettings: BrandingSettings,
    val statistics: ChannelStatistics,
    val image: BrandingImage? = null
)

data class ChannelContent(val relatedPlaylists: ChannelPlaylists)

data class ChannelPlaylists(val likes: String, val favorites: String, val uploads: String)

data class BrandingSettings(val image: BrandingImage)

data class ChannelStatistics(val subscriberCount: Int, val viewCount: Int)

data class BrandingImage(val bannerExternalUrl: String? = null)

data class ChannelDetails(
    val type: String,
    val title: String,
    val description: String,
    val position: Int,
    val thumbnails: Thumbnails
)

data class Thumbnails(val high: ThumbSettings)

data class ThumbSettings(val url: String, val width: Int, val height: Int)