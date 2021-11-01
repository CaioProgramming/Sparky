package com.silent.sparky.presentation.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.silent.core.data.model.podcast.PodcastHeader
import com.silent.core.data.model.podcast.podcastSections
import com.silent.core.utils.WebUtils
import com.silent.sparky.R
import com.silent.sparky.databinding.VideoGroupLayoutBinding

class VideoHeaderAdapter(val programSections: podcastSections, val headerSelected: ((Int) -> Unit)? = null): RecyclerView.Adapter<VideoHeaderAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(private val videoGroupLayoutBinding: VideoGroupLayoutBinding) : RecyclerView.ViewHolder(videoGroupLayoutBinding.root) {

        fun bind() {
            programSections[adapterPosition].run {
                programIcon?.let {
                    Glide.with(itemView.context).load(it).into(videoGroupLayoutBinding.programIcon)
                    videoGroupLayoutBinding.programIcon.fadeIn()
                }
                if (headerSelected == null) {
                    videoGroupLayoutBinding.headerCard.setOnClickListener {
                        WebUtils(videoGroupLayoutBinding.root.context).openYoutubePlaylist(playlistId)
                    }
                } else {
                    videoGroupLayoutBinding.headerCard.setOnClickListener { _ ->
                        headerSelected.invoke(adapterPosition)
                    }
                }
                videoGroupLayoutBinding.title.text = title
                videoGroupLayoutBinding.videosRecycler.adapter = VideosAdapter(videos)
                videoGroupLayoutBinding.videosRecycler.layoutManager = LinearLayoutManager(videoGroupLayoutBinding.root.context, orientation, false)
                videoGroupLayoutBinding.root.fadeIn()
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

    fun updateSection(programHeader: PodcastHeader) {
        programSections.add(programHeader)
        notifyItemInserted(itemCount)
     }
}