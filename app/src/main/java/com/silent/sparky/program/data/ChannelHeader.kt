package com.silent.sparky.program.data

import com.silent.core.program.Program
import com.silent.core.youtube.PlaylistResource

typealias channelsHeadings = ArrayList<ChannelHeader>
data class ChannelHeader(val program: Program, var uploads: List<PlaylistResource>)
