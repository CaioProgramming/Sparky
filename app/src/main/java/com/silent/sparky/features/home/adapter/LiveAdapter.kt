package com.silent.sparky.features.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.sparky.R
import com.silent.sparky.features.home.data.LiveHeader
import kotlinx.android.synthetic.main.live_podcast_expanded_layout.view.*
import kotlinx.android.synthetic.main.live_podcasts_layout.view.*

class LiveAdapter(
    val lives: ArrayList<LiveHeader>,
    var expanded: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class ExpandedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            lives[adapterPosition].run {
                loadVideo(video.id.videoId)
                itemView.live_title.text = video.snippet.title
            }
        }

        fun loadVideo(videoID: String) {
        }

    }

    inner class LiveViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            lives[bindingAdapterPosition].run {
                Glide.with(itemView.context)
                    .load(podcast.iconURL)
                    .into(itemView.podcastIcon)
                itemView.podcastName.text = podcast.name
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (expanded) ExpandedViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.live_podcast_expanded_layout, parent, false)
        ) else LiveViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.live_podcast_expanded_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LiveViewHolder) {
            holder.bind()
        } else {
            (holder as ExpandedViewHolder).bind()
        }
    }

    override fun getItemCount() = lives.size

}