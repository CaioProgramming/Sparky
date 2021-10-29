package com.silent.sparky.presentation.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.data.program.ProgramHeader
import com.silent.core.data.program.programSections
import com.silent.core.utils.WebUtils
import com.silent.sparky.R
import com.silent.sparky.databinding.VideoGroupLayoutBinding

class VideoHeaderAdapter(val programSections: programSections): RecyclerView.Adapter<VideoHeaderAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(val videoGroupLayoutBinding: VideoGroupLayoutBinding) : RecyclerView.ViewHolder(videoGroupLayoutBinding.root) {

        fun bind() {
            programSections[adapterPosition].run {
                videoGroupLayoutBinding.title.text = title
                videoGroupLayoutBinding.seeMoreButton.setOnClickListener {
                    WebUtils(videoGroupLayoutBinding.root.context).openYoutubePlaylist(playlistId)
                }
                videoGroupLayoutBinding.videosRecycler.adapter = VideosAdapter(videos)
                videoGroupLayoutBinding.videosRecycler.layoutManager = LinearLayoutManager(videoGroupLayoutBinding.root.context, orientation, false)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val bind = DataBindingUtil.inflate<VideoGroupLayoutBinding>(LayoutInflater.from(parent.context), R.layout.video_group_layout, parent, false)
        return HeaderViewHolder(bind)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
       holder.bind()
    }

    override fun getItemCount() = programSections.count()

    fun updateSection(programHeader: ProgramHeader) {
        programSections.add(programHeader)
        notifyItemInserted(itemCount)
    }

}