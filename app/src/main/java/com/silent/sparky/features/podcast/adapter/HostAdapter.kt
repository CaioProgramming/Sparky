package com.silent.sparky.features.podcast.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.podcast.Host
import com.silent.sparky.R
import kotlinx.android.synthetic.main.host_layout.view.*


class HostAdapter(val hosts: List<Host>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val NORMAL_VIEW = 2
    val REVERSE_VIEW = 1

    inner class HostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val context = itemView.context
            val host = hosts[adapterPosition]
            Glide.with(context).load(host.profilePic).into(itemView.host_photo)
            itemView.host_name.text = host.name
        }
    }

    inner class ReverseHostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val context = itemView.context
            val host = hosts[adapterPosition]
            Glide.with(context).load(host.profilePic).into(itemView.host_photo)
            itemView.host_name.text = host.name
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) REVERSE_VIEW else NORMAL_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostViewHolder {
        val layout =
            if (viewType == REVERSE_VIEW) R.layout.host_layout_reverse else R.layout.host_layout
        val view =
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return HostViewHolder(view)
    }


    override fun getItemCount() = hosts.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HostViewHolder) {
            holder.bind()
        } else {
            (holder as ReverseHostViewHolder).bind()

        }
    }

}