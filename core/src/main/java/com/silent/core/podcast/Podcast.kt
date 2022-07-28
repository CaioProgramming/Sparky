package com.silent.core.podcast

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import com.silent.ilustriscore.core.bean.BaseBean
import kotlinx.parcelize.Parcelize

typealias podcasts = ArrayList<Podcast>

@IgnoreExtraProperties
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
    var highLightColor: String = "#000",
    var cover: String? = "",
    var notificationIcon: String = "",
    var slogan: String = "",
    var liveTime: Int = 0,
    @Exclude var updating: Boolean = false
) : BaseBean(id) {


    companion object {
        val newPodcast = Podcast(id = NEW_PODCAST, name = "Adicionar podcast")
    }
}

const val NEW_PODCAST = "NEWPODCAST"
const val NEW_HOST = "NEWHOST"

@Parcelize
data class Host(
    var name: String = "",
    var profilePic: String = "",
    @SerializedName("user")
    var description: String = "",
    var socialUrl: String = "",
) : Parcelable {

    companion object {
        val NEWHOST = Host(NEW_HOST)
    }
}