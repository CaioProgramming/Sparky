package com.silent.sparky.program.data

import androidx.recyclerview.widget.RecyclerView
import com.silent.core.youtube.PlaylistResource

typealias programSections = ArrayList<ProgramHeader>
data class ProgramHeader(val title: String,
                         val videos: List<PlaylistResource>,
                         val playlistId: String,
                         val orientation: Int = RecyclerView.VERTICAL)
