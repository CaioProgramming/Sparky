package com.silent.core.youtube

import com.google.type.DateTime
import java.util.*

data class PlaylistItemResponse(val items: List<PlaylistResource>)

data class PlaylistResource(val snippet: VideoSnippet, val id: String)

data class VideoSnippet(
    val publishedAt: Date,
    val title: String,
    val description: String,
    val thumbnails: ThumbnailData? = null,
    val resourceId: ItemContent
)


data class ItemContent(val videoId: String)

data class ThumbnailData(val standard: DefaultThumbnail? = null)

data class DefaultThumbnail(val url: String? = null)
