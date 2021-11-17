package com.silent.newpodcasts.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.silent.core.data.podcast.Podcast
import com.silent.core.databinding.ProgramIconLayoutBinding
import com.silent.newpodcasts.R

class PodcastAdapter(private val podcasts: ArrayList<Podcast>,
                     private val onSelectProgram: ((Podcast) -> Unit)? = null,
                     private val showName: Boolean = true): RecyclerView.Adapter<PodcastAdapter.ProgramViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val programIconLayoutBinding: ProgramIconLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.program_icon_layout, parent, false)
        return ProgramViewHolder(programIconLayoutBinding)
    }

    fun updateAdapter(podcast: Podcast) {
        podcasts.add(podcast)
        notifyItemInserted(itemCount)
    }


    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = podcasts.size

    inner class ProgramViewHolder(private val programIconLayoutBinding: ProgramIconLayoutBinding):
        RecyclerView.ViewHolder(programIconLayoutBinding.root) {

        fun bind() {
            val context = programIconLayoutBinding.root.context
            podcasts[adapterPosition].run {
                Glide.with(context).load(iconURL).into(programIconLayoutBinding.programIcon)
                programIconLayoutBinding.programName.text = name
                onSelectProgram?.let {
                    programIconLayoutBinding.programIcon.setOnClickListener { _ ->
                        it(this)
                    }
                }
                if (showName) programIconLayoutBinding.programName.fadeIn()
            }
        }

    }

}