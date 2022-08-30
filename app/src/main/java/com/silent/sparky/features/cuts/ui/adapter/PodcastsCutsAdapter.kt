package com.silent.sparky.features.cuts.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.ui.extensions.gone
import com.silent.core.databinding.PodcastIconLayoutBinding
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts
import com.silent.sparky.R

class PodcastsCutsAdapter(val podcasts: podcasts, val podcastSelect: (Podcast) -> Unit) :
    RecyclerView.Adapter<PodcastsCutsAdapter.PodcastViewHolder>() {

    val selectedPodcasts = ArrayList<String>()

    fun selectPodcast(podcast: Podcast) {
        if (selectedPodcasts.contains(podcast.id)) selectedPodcasts.remove(podcast.id) else selectedPodcasts.add(
            podcast.id
        )
        notifyItemChanged(podcasts.indexOf(podcast))
    }

    inner class PodcastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            PodcastIconLayoutBinding.bind(itemView).run {
                val podcast = podcasts[bindingAdapterPosition]
                podcastTitle.gone()
                Glide.with(programIcon).load(podcast.iconURL).into(programIcon)
                liveStatus.rimColor = root.context.getColor(R.color.material_grey900)
                if (selectedPodcasts.contains(podcast.id)) {
                    liveStatus.rimColor = Color.parseColor(podcast.highLightColor)
                }
                programIcon.setOnClickListener {
                    podcastSelect(podcast)
                }
                root.setOnClickListener {
                    podcastSelect(podcast)
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        return PodcastViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.podcast_icon_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = podcasts.size
}