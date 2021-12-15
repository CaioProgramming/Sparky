package com.silent.sparky.features.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.silent.core.flow.data.FlowLive
import com.silent.sparky.R
import kotlinx.android.synthetic.main.program_icon_layout.view.*

class LiveAdapter(
    val lives: List<FlowLive>,
    private val onSelectLive: (FlowLive) -> Unit
) : RecyclerView.Adapter<LiveAdapter.ProgramViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.program_icon_layout, parent, false)
        return ProgramViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = lives.size

    inner class ProgramViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind() {
            val context = itemView.context
            lives[adapterPosition].run {
                Glide.with(context).load(cover).into(itemView.program_icon)
                //itemView.program_name.text = name
                itemView.program_icon.setOnClickListener {
                    onSelectLive(this)
                }
                itemView.live_status.fadeIn()
            }
        }
    }

}
