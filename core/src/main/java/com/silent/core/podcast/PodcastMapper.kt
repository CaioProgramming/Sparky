package com.silent.core.podcast

import com.silent.core.youtube.ChannelResource

class PodcastMapper {

    fun mapChannelResponse(channel: ChannelResource) = Podcast(
        youtubeID = channel.id,
        name = channel.snippet.title,
        iconURL = channel.snippet.thumbnails.high.url,
        subscribe = channel.statistics.subscriberCount,
        views = channel.statistics.viewCount,
        uploads = channel.contentDetails.relatedPlaylists.uploads,
    )

}