package com.silent.sparky.features.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.slideInBottom
import com.silent.core.stickers.response.Badge
import com.silent.sparky.R
import com.silent.sparky.databinding.BadgeLayoutBinding

class BadgeAdapter(val selectedBadges: List<Badge>) :
    RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            val badge = selectedBadges[absoluteAdapterPosition]
            BadgeLayoutBinding.bind(itemView).run {
                Glide.with(itemView.context)
                    .load(badge.high)
                    .error(R.drawable.ic_iconmonstr_connection_1)
                    .into(badgeImage)
                root.contentDescription = badge.description
                root.slideInBottom()
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