package com.silent.core.podcast

import com.google.gson.annotations.SerializedName
import com.silent.ilustriscore.core.bean.BaseBean
import java.io.Serializable
import java.util.*

typealias podcasts = ArrayList<Podcast>

data class Podcast(
    override var id: String = "",
    var name: String = "",
    var subscribe: Int = 0,
    @SerializedName("thumbnail_url")
    var iconURL: String = "",
    var views: Int = 0,
    var hosts: ArrayList<Host> = ArrayList(),
    var weeklyGuests: ArrayList<Host> = ArrayList(),
    var youtubeID: String = "",
    var cuts: String = "",
    var uploads: String = "",
    var highLightColor: String = "#000",
    var cover: String = ""
) : BaseBean(id) {
    companion object {
        val newPodcast = Podcast(id = NEW_PODCAST, name = "Adicionar podcast")
    }
}

const val NEW_PODCAST = "NEWPODCAST"
const val NEW_HOST = "NEWHOST"

data class Host(
    var name: String = "",
    var profilePic: String = "",
    @SerializedName("user")
    var description: String = "",
    var socialUrl: String = "",
    var comingDate: Date? = null
) : Serializable {

    companion object {
        val NEWHOST = Host(NEW_HOST)
    }
}