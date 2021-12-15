package com.silent.core.flow.data

import java.io.Serializable

data class FlowLivesResponse(val lives: List<FlowLive>)

data class FlowLive(val title: String, val cover: String, val feed: LiveFeed) : Serializable

data class LiveFeed(val youtube: String)