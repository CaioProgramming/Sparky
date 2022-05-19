package com.silent.core.youtube

data class SearchResponse(
    val items: List<SearchItem>
)

data class SearchItem(
    val id: IdDetails,
    val snippet: VideoSnippet
)

data class Details(val title: String, val description: String)

data class IdDetails(
    val kind: String,
    val videoId: String
)