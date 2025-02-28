package com.silent.core.videos

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import com.silent.core.podcast.Podcast
import com.silent.ilustriscore.core.bean.BaseBean
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
data class Video(
    var key: String = "",
    var description: String = "",
    var podcastId: String = "",
    var publishedAt: Date = Date(),
    @SerializedName("thumbnail_url")
    var thumbnailUrl: String = "",
    @SerializedName("ytId")
    var youtubeID: String = "",
    var title: String = "",
    @Exclude var podcast: Podcast? = null,
    @Exclude var videoType: VideoType = VideoType.DEFAULT
) : Serializable, BaseBean(key) {
    init {
        key = id
    }
}

enum class VideoType {
    DEFAULT, MEDIUM, BIG
}