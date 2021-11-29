package com.silent.sparky.manager.features.manager.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts
import com.silent.sparky.manager.R
import kotlinx.android.synthetic.main.podcasts_card.view.*

class PodcastManagerAdapter(val podcasts: podcasts, val onSelectPodcast: (Podcast) -> Unit) :
    RecyclerView.Adapter<PodcastManagerAdapter.PodcastViewHolder>() {

    inner class PodcastViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            podcasts[bindingAdapterPosition].run {
                itemView.podcast_name.text = name
                Glide.with(itemView.context).load(iconURL).into(itemView.podcast_icon)
                itemView.podcast_card.setOnClickListener {
                    onSelectPodcast(this)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        return PodcastViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.podcasts_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = podcasts.size

}