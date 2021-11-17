package com.silent.core.data.model.youtube

typealias relatedChannels  = List<String>

data class ChannelSectionListResponse(val items: List<SectionItem>)

data class SectionItem(val snippet: SectionSnippet, val contentDetails: HashMap<String, Any>)

data class SectionSnippet(val type: String)
