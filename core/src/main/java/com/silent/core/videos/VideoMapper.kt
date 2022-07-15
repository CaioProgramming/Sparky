package com.silent.core.videos

import com.silent.core.youtube.VideoSnippet

class VideoMapper {

    fun mapVideoSnippet(videoItem: VideoSnippet, podcastId: String) = Video(
        id = videoItem.resourceId.videoId,
        description = videoItem.description,
        publishedAt = videoItem.publishedAt,
        thumbnailUrl = videoItem.thumbnails?.standard?.url ?: "",
        youtubeID = videoItem.resourceId.videoId,
        title = videoItem.title,
        podcastId = podcastId
    )
    fun mapLiveSnippet(videoId: String,  videoItem: VideoSnippet, podcastId: String) = Video(
        id = videoId,
        description = videoItem.description,
        publishedAt = videoItem.publishedAt,
        thumbnailUrl = videoItem.thumbnails?.standard?.url ?: "",
        youtubeID = videoId,
        title = videoItem.title,
        podcastId = podcastId
    )


}