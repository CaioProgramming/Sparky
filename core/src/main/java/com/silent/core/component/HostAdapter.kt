package com.silent.core.component

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.R
import com.silent.core.databinding.HostCardBinding
import com.silent.core.databinding.HostCardReverseBinding
import com.silent.core.databinding.NewHostLayoutBinding
import com.silent.core.podcast.Host
import com.silent.core.podcast.NEW_HOST
import com.silent.core.utils.ImageUtils


class HostAdapter(
    val hosts: ArrayList<Host>,
    val isEdit: Boolean = false,
    val hostSelected: (Host) -> Unit,
    val highLightColor: String? = null,
    val groupType: GroupType = GroupType.HOSTS
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val NORMAL_VIEW = 2
    private val REVERSE_VIEW = 1
    val NEW_HOST_VIEW = 3

    init {
        if (isEdit) hosts.add(Host.NEWHOST)
    }

    inner class NewHostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            NewHostLayoutBinding.bind(itemView).run {
                addHostButton.text = "Adicionar novo Host"
                Glide.with(itemView.context).load(ImageUtils.getRandomHostPlaceHolder())
                    .into(hostPlaceHolder)
                addHostButton.setOnClickListener {
                    hostSelected(hosts[bindingAdapterPosition])
                }
                if (!highLightColor.isNullOrEmpty()) {
                    hostPlaceHolder.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor(highLightColor))
                    addHostButton.setTextColor(
                        ColorStateList.valueOf(
                            Color.parseColor(
                                highLightColor
                            )
                        )
                    )
                }
            }
        }
    }

    inner class HostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            HostCardBinding.bind(itemView).run {
                val host = hosts[bindingAdapterPosition]
                itemView.setOnClickListener {
                    hostSelected(host)
                }
                Glide.with(itemView.context).load(host.profilePic).error(R.drawable.ic_iconmonstr_connection_1).into(hostPhoto)
                hostName.text = host.name
                hostDescription.text = host.description
                if (!highLightColor.isNullOrEmpty()) {
                    hostNameCard.strokeColor = Color.parseColor(highLightColor)
                    hostCard.strokeColor = Color.parseColor(highLightColor)
                    hostName.setTextColor(Color.parseColor(highLightColor))
                }
            }

        }
    }

    inner class ReverseHostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            HostCardReverseBinding.bind(itemView).run {
                val context = itemView.context
                val host = hosts[adapterPosition]
                itemView.setOnClickListener {
                    hostSelected(host)
                }
                Glide.with(context)
                    .load(host.profilePic)
                    .error(R.drawable.ic_iconmonstr_connection_1)
                    .into(hostPhoto)
                hostName.text = host.name
                hostDescription.text = host.description
                if (!highLightColor.isNullOrEmpty()) {
                    hostNameCard.strokeColor = Color.parseColor(highLightColor)
                    hostCard.strokeColor = Color.parseColor(highLightColor)
                    hostName.setTextColor(Color.parseColor(highLightColor))
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when {
            hosts[position].name == NEW_HOST -> NEW_HOST_VIEW
            position % 2 == 0 -> REVERSE_VIEW
            else -> NORMAL_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NEW_HOST_VIEW -> NewHostViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.new_host_layout, parent, false)
            )
            REVERSE_VIEW -> ReverseHostViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.host_card_reverse, parent, false)
            )
            else -> HostViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.host_card, parent, false)
            )
        }
    }

    override fun getItemCount() = hosts.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewHostViewHolder -> {
                holder.bind()
            }
            is HostViewHolder -> {
                holder.bind()
            }
            is ReverseHostViewHolder -> {
                holder.bind()
            }
        }
    }

    fun updateHost(host: Host) {
        hosts.add(host)
        notifyItemInserted(itemCount)
    }

    fun refresh(hosts: ArrayList<Host>) {
        hosts.clear()
        hosts.addAll(hosts)
        if (isEdit) hosts.add(Host.NEWHOST)
        notifyDataSetChanged()
    }

}