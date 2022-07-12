package com.silent.sparky.features.home.data

import androidx.recyclerview.widget.RecyclerView
import com.silent.core.videos.Video
import java.io.Serializable

typealias programSections = ArrayList<PodcastHeader>

data class PodcastHeader(
    val title: String,
    val icon: String? = null,
    val highLightColor: String? = null,
    val videos: ArrayList<Video>,
    val playlistId: String,
    val channelURL: String? = null,
    val orientation: Int = RecyclerView.VERTICAL,
    val seeMore: Boolean = false,
    var referenceIndex: Int? = null,
    var subTitle: String? = null
): Serializable
