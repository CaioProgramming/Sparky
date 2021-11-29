package com.silent.core.podcast

import com.google.gson.annotations.SerializedName
import com.silent.ilustriscore.core.bean.BaseBean
import java.io.Serializable

typealias podcasts = ArrayList<Podcast>
data class Podcast(
    override var id: String = "",
    var name: String = "",
    var subscribe: Int = 0,
    @SerializedName("thumbnail_url")
    var iconURL: String = "",
    var views: Int = 0,
    var hosts: List<Host> = emptyList(),
    var instagram: String = "",
    var twitch: String = "",
    var twitter: String = "",
    var youtubeID: String = "",
    var cuts: String = "",
) : BaseBean(id)

data class Host(val name: String, val profilePic: String) : Serializable