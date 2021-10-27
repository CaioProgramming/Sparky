package com.silent.core.youtube

import java.util.*

data class PlaylistItemResponse(val items: List<PlaylistResource>)

data class PlaylistResource(val snippet: PlaylistSnippet, val id: String)

data class PlaylistSnippet(val publishedAt: Date, val title: String,
                           val description: String,
                           val thumbnails: ThumbnailData,
                           val resourceId: ItemContent)


data class ItemContent(val videoId: String)

data class ThumbnailData(val standard: DefaultThumbnail)

data class DefaultThumbnail(val url: String? = null)
