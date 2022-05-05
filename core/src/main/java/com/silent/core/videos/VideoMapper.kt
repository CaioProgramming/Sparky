package com.silent.core.videos

import com.silent.core.youtube.VideoSnippet

class VideoMapper {

    fun mapVideoSnippet(videoItem: VideoSnippet, podcast: String) = Video(
        id = videoItem.resourceId.videoId,
        description = videoItem.description,
        publishedAt = videoItem.publishedAt.toString(),
        thumbnailUrl = videoItem.thumbnails?.standard?.url ?: "",
        youtubeID = videoItem.resourceId.videoId,
        title = videoItem.title,
        podcastId = podcast
    )


}