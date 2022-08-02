package com.silent.core.podcast

import com.silent.core.youtube.ChannelResource

class PodcastMapper {

    fun mapChannelResponse(channel: ChannelResource) = Podcast(
        name = channel.snippet.title,
        subscribe = channel.statistics.subscriberCount,
        iconURL = channel.snippet.thumbnails.high.url,
        views = channel.statistics.viewCount,
        youtubeID = channel.id,
        uploads = channel.contentDetails.relatedPlaylists.uploads,
    )

}