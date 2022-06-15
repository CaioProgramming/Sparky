package com.silent.core.videos

import com.google.gson.annotations.SerializedName
import com.google.type.DateTime
import com.silent.ilustriscore.core.bean.BaseBean
import java.util.*

data class Video(
    override var id: String = "",
    var description: String = "",
    var podcastId: String = "",
    var publishedAt: Date = Date(),
    @SerializedName("thumbnail_url")
    var thumbnailUrl: String = "",
    @SerializedName("ytId")
    var youtubeID: String = "",
    var title: String = ""

) : BaseBean(id)