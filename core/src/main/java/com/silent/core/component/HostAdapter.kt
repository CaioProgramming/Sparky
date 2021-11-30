package com.silent.core.component

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.R
import com.silent.core.podcast.Host
import com.silent.core.podcast.NEW_HOST
import kotlinx.android.synthetic.main.host_layout.view.*


class HostAdapter(val hosts: ArrayList<Host>, val hostSelected: (Host) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val NORMAL_VIEW = 2
    val REVERSE_VIEW = 1

    inner class HostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val context = itemView.context
            val host = hosts[adapterPosition]
            itemView.setOnClickListener {
                hostSelected(host)
            }
            if (host.name != NEW_HOST) {
                Glide.with(context).load(host.profilePic).into(itemView.host_photo)
                itemView.host_name.text = host.name
            } else {
                itemView.host_photo.borderColor = Color.TRANSPARENT
                itemView.host_photo.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.ic_iconmonstr_plus
                    )
                )

            }
        }
    }

    inner class ReverseHostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val context = itemView.context
            val host = hosts[adapterPosition]
            if (host.name != NEW_HOST) {
                Glide.with(context).load(host.profilePic).into(itemView.host_photo)
                itemView.host_name.text = host.name
            } else {
                itemView.host_photo.borderColor =
                    ContextCompat.getColor(itemView.context, R.color.md_blue500)
                itemView.host_photo.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.ic_iconmonstr_plus
                    )
                )
            }
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

    fun updateHost(host: Host) {
        hosts.add(host)
        notifyItemInserted(itemCount)
    }

    fun refresh(hosts: ArrayList<Host>) {
        hosts.clear()
        hosts.addAll(hosts)
        notifyDataSetChanged()
    }

}