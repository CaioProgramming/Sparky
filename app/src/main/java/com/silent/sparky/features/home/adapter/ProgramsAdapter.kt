package com.silent.sparky.features.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.program.Podcast
import com.silent.sparky.R
import kotlinx.android.synthetic.main.fragment_podcast.view.*

class ProgramsAdapter(
    val podcasts: List<Podcast>,
    private val onSelectProgram: (Podcast) -> Unit
) : RecyclerView.Adapter<ProgramsAdapter.ProgramViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.program_icon_layout, parent, false)
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
            podcasts[adapterPosition].run {
                Glide.with(context).load(iconURL).into(itemView.program_icon)
                itemView.program_name.text = name
                itemView.program_icon.setOnClickListener {
                    onSelectProgram(this)
                }
            }
        }

    }

}