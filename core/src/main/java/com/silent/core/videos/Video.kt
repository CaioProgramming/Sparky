package com.silent.core.videos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import com.silent.core.podcast.Podcast
import com.silent.ilustriscore.core.bean.BaseBean

@IgnoreExtraProperties
data class Video(
    var key: String = "",
    var description: String = "",
    var podcastId: String = "",
    var publishedAt: Timestamp = Timestamp.now(),
    @SerializedName("thumbnail_url")
    var thumbnailUrl: String = "",
    @SerializedName("ytId")
    var youtubeID: String = "",
    var title: String = "",
    @Exclude var podcast: Podcast? = null,
    @Exclude var videoType: VideoType = VideoType.DEFAULT
) : BaseBean(key) {
    init {
        key = id
    }
}

enum class VideoType {
    DEFAULT, MEDIUM, BIG
}