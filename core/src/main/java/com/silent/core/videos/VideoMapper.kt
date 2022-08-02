package com.silent.core.videos

import com.silent.core.utils.ImageUtils
import com.silent.core.youtube.response.VideoSnippet

class VideoMapper {

    fun mapVideoSnippet(videoItem: VideoSnippet, podcastId: String) = Video(
        key = videoItem.resourceId.videoId,
        description = videoItem.description,
        publishedAt = videoItem.publishedAt,
        thumbnailUrl = ImageUtils.getYoutubeThumb(videoItem.resourceId.videoId),
        youtubeID = videoItem.resourceId.videoId,
        title = videoItem.title,
        podcastId = podcastId
    )

    fun mapLiveSnippet(videoId: String, videoItem: VideoSnippet, podcastId: String) = Video(
        key = videoId,
        description = videoItem.description,
        publishedAt = videoItem.publishedAt,
        thumbnailUrl = ImageUtils.getYoutubeThumb(videoId),
        youtubeID = videoId,
        title = videoItem.title,
        podcastId = podcastId
    )


}