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
    var hosts: ArrayList<Host> = ArrayList(),
    var youtubeID: String = "",
    var cuts: String = "",
    var uploads: String = "",
    var highLightColor: String = ""
) : BaseBean(id) {
    companion object {
        val newPodcast = Podcast(id = NEW_PODCAST, name = "Adicionar podcast")
    }
}

const val NEW_PODCAST = "NEWPODCAST"
const val NEW_HOST = "NEWHOST"

data class Host(var name: String = "", var profilePic: String = "", var user: String = "") :
    Serializable {

    companion object {
        val NEWHOST = Host(NEW_HOST)
    }
}