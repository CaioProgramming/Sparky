package com.silent.sparky.features.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.flow.data.FlowBadge
import com.silent.core.utils.ImageUtils
import com.silent.sparky.R
import com.silent.sparky.databinding.BadgeLayoutBinding

class BadgeAdapter(val selectedBadges: List<FlowBadge>) :
    RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            val badge = selectedBadges[absoluteAdapterPosition]
            BadgeLayoutBinding.bind(itemView).run {
                Glide.with(itemView.context)
                    .load(badge.src)
                    .error(ImageUtils.getRandomIcon())
                    .into(badgeImage)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        return BadgeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.badge_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return selectedBadges.count()
    }


}