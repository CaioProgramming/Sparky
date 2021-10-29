package com.silent.core.data.program

import com.silent.core.youtube.PlaylistResource

typealias channelsHeadings = ArrayList<ChannelHeader>
data class ChannelHeader(val podcast: Podcast, var uploads: List<PlaylistResource>)
