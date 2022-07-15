package com.silent.core.youtube

data class ChannelSectionResponse(val items: List<SectionItem>)

data class SectionItem(val snippet: SectionSnippet, val contentDetails: HashMap<String, Any>)

data class SectionSnippet(val type: String, val title: String)
