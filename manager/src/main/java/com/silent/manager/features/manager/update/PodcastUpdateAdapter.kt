package com.silent.manager.features.manager.update

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.ilustris.ui.extensions.gone
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts
import com.silent.manager.R
import com.silent.manager.databinding.PodcastHorizontalLayoutBinding

class PodcastUpdateAdapter(val podcasts: podcasts): RecyclerView.Adapter<PodcastUpdateAdapter.PodcastHorizontalHolder>() {


    inner class PodcastHorizontalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            PodcastHorizontalLayoutBinding.bind(itemView).run {
                val podcast = podcasts[bindingAdapterPosition]
                val hightlightColor = Color.parseColor(podcast.highLightColor)
                Glide.with(root.context).load(podcast.iconURL).into(programIcon)
                podcastTitle.text = podcast.name
                programIcon.borderColor = hightlightColor
                updateProgress.rimColor = hightlightColor
                if (podcast.updating) {
                    updateCheck.gone()
                    updateProgress.indeterminate = true
                } else {
                    updateCheck.fadeIn()
                    updateProgress.progress = 100
                }
                root.fadeIn()
            }
        }

    }

    fun updatePodcast(position: Int, updating: Boolean) {
        podcasts[position].updating = updating
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastHorizontalHolder {
        return PodcastHorizontalHolder(LayoutInflater.from(parent.context).inflate(R.layout.podcast_horizontal_layout, parent, false))
    }

    override fun onBindViewHolder(holder: PodcastHorizontalHolder, position: Int) {
       holder.bind()
    }

    override fun getItemCount(): Int = podcasts.size


}