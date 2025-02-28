package com.silent.core.component

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.extensions.gone
import com.ilustris.ui.extensions.visible
import com.silent.core.R
import com.silent.core.databinding.PodcastIconLayoutBinding
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts

class PodcastAdapter(
    val podcasts: podcasts,
    val showTitle: Boolean = true,
    val onSelectPodcast: (Podcast) -> Unit
) :
    RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.podcast_icon_layout, parent, false)
        return PodcastViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = podcasts.size

    inner class PodcastViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind() {
            PodcastIconLayoutBinding.bind(itemView).run {
                val context = itemView.context
                val podcast = podcasts[adapterPosition]
                Glide.with(context).load(podcast.iconURL).into(programIcon)
                podcastTitle.text = podcast.name
                programIcon.setOnClickListener {
                    onSelectPodcast(podcast)
                }
                programIcon.slideInBottom()
                liveStatus.visible()
                liveStatus.rimColor = Color.parseColor(podcast.highLightColor)
                liveStatus.indeterminate = podcast.isLive
                liveStatus.progress = 100
                itemView.slideInBottom()
                if (showTitle) podcastTitle.visible() else podcastTitle.gone()
            }
        }

    }
}