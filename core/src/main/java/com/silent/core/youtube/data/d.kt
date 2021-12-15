package com.silent.core.youtube.data

data class YoutubeVideoResponse(val items: List<VideoItem>)
data class VideoItem(val snippet: VideoSnippet)
data class VideoSnippet(val channelId: String)