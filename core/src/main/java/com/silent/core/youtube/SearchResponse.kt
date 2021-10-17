package com.silent.core.youtube

import com.google.gson.annotations.SerializedName
import com.google.type.DateTime

data class SearchResponse(
    val kind: String,
    val etag: String,
    @SerializedName("id")
    val idDetails: idDetails,
    val snippet: SearchDTO
)
data class idDetails(val kind: String, val videoId: String, val channelId: String, val playlistId: String)

data class SearchDTO(val publishedAt: DateTime,
                     val channelId: String,
                     val title: String,
                     val description: String,
                     val channelTitle: String)