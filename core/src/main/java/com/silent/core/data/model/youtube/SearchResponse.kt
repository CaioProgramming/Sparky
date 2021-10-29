package com.silent.core.data.model.youtube

import com.google.gson.annotations.SerializedName
import com.google.type.DateTime

data class SearchResponse(
    val items: List<SearchItem>
)
data class SearchItem(
    val id: IdDetails,
)

data class IdDetails(val kind: String,
                     val videoId: String)
