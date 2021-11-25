package com.silent.sparky.features.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silent.sparky.R
import com.silent.sparky.features.podcast.adapter.VideosAdapter
import com.silent.sparky.features.podcast.data.PodcastHeader
import com.silent.sparky.features.podcast.data.programSections
import kotlinx.android.synthetic.main.video_group_layout.view.*

class VideoHeaderAdapter(
    val programSections: programSections,
    val headerSelected: (PodcastHeader) -> Unit
) : RecyclerView.Adapter<VideoHeaderAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            programSections[adapterPosition].run {
                itemView.group_title.text = title
                itemView.see_more_button.setOnClickListener {
                    headerSelected(this)
                    //WebUtils(videoGroupLayoutBinding.root.context).openYoutubePlaylist(playlistId)
                }
                itemView.videos_recycler.adapter = VideosAdapter(videos)
                itemView.videos_recycler.layoutManager =
                    LinearLayoutManager(itemView.context, orientation, false)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.video_group_layout, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = programSections.count()

    fun updateSection(podcastHeader: PodcastHeader) {
        programSections.add(podcastHeader)
        notifyItemInserted(itemCount)
    }

}