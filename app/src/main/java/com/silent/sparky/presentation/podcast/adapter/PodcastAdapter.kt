package com.silent.sparky.presentation.podcast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.silent.core.data.program.Podcast
import com.silent.core.databinding.ProgramIconLayoutBinding
import com.silent.sparky.R

class PodcastAdapter(val podcasts: List<Podcast>,
                     private val onSelectProgram: (Podcast) -> Unit,
                     private val showName: Boolean = true): RecyclerView.Adapter<PodcastAdapter.ProgramViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val programIconLayoutBinding: ProgramIconLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.program_icon_layout, parent, false)
        return ProgramViewHolder(programIconLayoutBinding)
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
                programIconLayoutBinding.programIcon.setOnClickListener {
                    onSelectProgram(this)
                }
                if (showName) programIconLayoutBinding.programName.fadeIn()
            }
        }

    }

}