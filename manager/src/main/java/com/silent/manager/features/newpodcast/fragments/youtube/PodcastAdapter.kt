package com.silent.manager.features.newpodcast.fragments.youtube

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.slideInBottom
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts
import com.silent.manager.R
import kotlinx.android.synthetic.main.program_icon_layout.view.*

class PodcastAdapter(val podcasts: podcasts, val onSelectPodcast: (Podcast) -> Unit) :
    RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.program_icon_layout, parent, false)
        return PodcastViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        holder.bind()
    }

    fun updateAdapter(podcast: Podcast) {
        podcasts.add(podcast)
        notifyItemInserted(podcasts.lastIndex)
    }


    override fun getItemCount() = podcasts.size

    inner class PodcastViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind() {
            val context = itemView.context
            podcasts[adapterPosition].run {
                Glide.with(context).load(iconURL).into(itemView.program_icon)
                //itemView.program_name.text = name
                itemView.program_icon.setOnClickListener {
                    onSelectPodcast(this)
                }
                itemView.program_icon.slideInBottom()
            }
        }

    }
}