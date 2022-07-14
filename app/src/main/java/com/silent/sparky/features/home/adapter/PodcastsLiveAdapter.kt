package com.silent.sparky.features.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.extensions.gone
import com.ilustris.ui.extensions.visible
import com.silent.core.podcast.Podcast
import com.silent.sparky.R
import com.silent.sparky.databinding.PodcastLiveLayoutBinding

class PodcastsLiveAdapter(
    val podcasts: List<Podcast>,
    val isLive: Boolean = false,
    private val onSelectProgram: (Podcast, Int) -> Unit
) : RecyclerView.Adapter<PodcastsLiveAdapter.ProgramViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.podcast_live_layout, parent, false)
        return ProgramViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = podcasts.size

    inner class ProgramViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind() {
            val context = itemView.context
            val podcast = podcasts[bindingAdapterPosition]
            PodcastLiveLayoutBinding.bind(itemView).run {
                Glide.with(context).load(podcast.iconURL).into(programIcon)
                //itemView.program_name.text = name
                programIcon.setOnClickListener {
                    onSelectProgram(podcast, bindingAdapterPosition)
                }
                if (isLive) {
                    liveStatus.visible()
                    liveStatus.indeterminate = true
                    if (podcast.highLightColor.isNotEmpty()) {
                        liveStatus.rimColor = Color.parseColor(podcast.highLightColor)
                    }
                } else {
                    if (podcast.highLightColor.isNotEmpty()) {
                        programIcon.borderColor = Color.parseColor(podcast.highLightColor)
                    }
                    liveStatus.gone()
                    itemView.slideInBottom()
                }
            }

        }

    }

}