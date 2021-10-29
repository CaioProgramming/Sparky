package com.silent.sparky.presentation.podcast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.data.program.Host
import com.silent.sparky.databinding.HostLayoutBinding

class HostAdapter(val hosts: List<Host>) : RecyclerView.Adapter<HostAdapter.HostViewHolder>(){

    inner class HostViewHolder(private val hostLayoutBinding: HostLayoutBinding) : RecyclerView.ViewHolder(hostLayoutBinding.root) {
        fun bind() {
            hostLayoutBinding.run {
                val context = root.context
                val host = hosts[adapterPosition]
                Glide.with(context).load(host.profilePic).into(hostPhoto)
                hostName.text = host.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostViewHolder {
       val hostLayoutBinding = HostLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HostViewHolder(hostLayoutBinding)
    }

    override fun onBindViewHolder(holder: HostViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = hosts.size

}