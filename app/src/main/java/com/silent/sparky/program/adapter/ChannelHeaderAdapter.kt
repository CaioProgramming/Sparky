package com.silent.sparky.program.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.ilustriscore.core.utilities.visible
import com.silent.sparky.R
import com.silent.sparky.databinding.VideoGroupLayoutBinding
import com.silent.sparky.program.ProgramActivity
import com.silent.sparky.program.data.ChannelHeader
import com.silent.sparky.program.data.channelsHeadings

class ChannelHeaderAdapter(val channelSections: channelsHeadings): RecyclerView.Adapter<ChannelHeaderAdapter.ChannelHeaderViewHolder>() {

    inner class ChannelHeaderViewHolder(private val videoGroupLayoutBinding: VideoGroupLayoutBinding) : RecyclerView.ViewHolder(videoGroupLayoutBinding.root) {

        fun bind() {
            channelSections[adapterPosition].run {
                videoGroupLayoutBinding.title.text = program.name
                videoGroupLayoutBinding.seeMoreButton.setOnClickListener {
                    ProgramActivity.getLaunchIntent(program, itemView.context)
                }
                videoGroupLayoutBinding.programIcon.visible()
                Glide.with(itemView.context).load(program.iconURL).into(videoGroupLayoutBinding.programIcon)
                videoGroupLayoutBinding.videosRecycler.adapter = VideosAdapter(uploads)
                videoGroupLayoutBinding.videosRecycler.layoutManager = LinearLayoutManager(videoGroupLayoutBinding.root.context,
                    RecyclerView.HORIZONTAL, false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelHeaderViewHolder {
        val bind = DataBindingUtil.inflate<VideoGroupLayoutBinding>(LayoutInflater.from(parent.context), R.layout.video_group_layout, parent, false)
        return ChannelHeaderViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ChannelHeaderViewHolder, position: Int) {
       holder.bind()
    }

    override fun getItemCount() = channelSections.count()

    fun updateSection(programHeader: ChannelHeader) {
        channelSections.add(programHeader)
        notifyItemInserted(itemCount)
    }

}