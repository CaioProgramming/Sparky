package com.silent.manager.features.manager.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts
import com.silent.manager.R
import com.silent.manager.databinding.PodcastsCardBinding

class PodcastManagerAdapter(val podcasts: podcasts, val onSelectPodcast: (Podcast) -> Unit) :
    RecyclerView.Adapter<PodcastManagerAdapter.PodcastViewHolder>() {

    inner class PodcastViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            PodcastsCardBinding.bind(itemView).run {
                val podcast = podcasts[bindingAdapterPosition]
                podcastName.text = podcast.name
                Glide.with(itemView.context).load(podcast.iconURL).into(podcastIcon)
                podcastCard.setOnClickListener {
                    onSelectPodcast(podcast)
                }
                if (podcast.highLightColor.isNotEmpty()) {
                    podcastIcon.borderColor = Color.parseColor(podcast.highLightColor)
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