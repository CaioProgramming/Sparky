package com.silent.sparky.features.podcast.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.program.Host
import com.silent.sparky.R
import kotlinx.android.synthetic.main.host_layout.view.*

class HostAdapter(val hosts: List<Host>) : RecyclerView.Adapter<HostAdapter.HostViewHolder>(){

    inner class HostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val context = itemView.context
            val host = hosts[adapterPosition]
            Glide.with(context).load(host.profilePic).into(itemView.host_photo)
            itemView.host_name.text = host.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostViewHolder {
        val hostLayoutBinding =
            LayoutInflater.from(parent.context).inflate(R.layout.host_layout, parent, false)
        return HostViewHolder(hostLayoutBinding)
    }

    override fun onBindViewHolder(holder: HostViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = hosts.size

}