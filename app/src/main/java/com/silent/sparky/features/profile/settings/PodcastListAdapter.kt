package com.silent.sparky.features.profile.settings

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.podcast.NEW_PODCAST
import com.silent.core.podcast.Podcast
import com.silent.ilustriscore.core.utilities.gone
import com.silent.ilustriscore.core.utilities.visible
import com.silent.sparky.R
import com.silent.sparky.databinding.PodcastListLayoutBinding

class PodcastListAdapter(
    val podcasts: List<Podcast>,
    private val onSelectProgram: (Podcast, Int) -> Unit
) : RecyclerView.Adapter<PodcastListAdapter.ProgramViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.podcast_list_layout, parent, false)
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
            val podcast = podcasts[adapterPosition]
            PodcastListLayoutBinding.bind(itemView).run {
                Glide.with(context).load(podcast.iconURL).into(programIcon)
                programName.text = podcast.name
                root.setOnClickListener {
                    onSelectProgram(podcast, bindingAdapterPosition)
                }
                if (podcast.id == NEW_PODCAST) {
                    programIcon.gone()
                    programName.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    programName.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.material_yellow800
                        )
                    )
                } else {
                    programIcon.visible()
                }
                if (bindingAdapterPosition == podcasts.size - 1) {
                    root.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                }

            }
        }
    }

}