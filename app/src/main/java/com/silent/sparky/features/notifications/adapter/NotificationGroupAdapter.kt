package com.silent.sparky.features.notifications.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.silent.core.notifications.Notification
import com.silent.core.podcast.Podcast
import com.silent.sparky.R
import com.silent.sparky.databinding.NotificationGroupLayoutBinding
import com.silent.sparky.databinding.PodcastListLayoutBinding
import com.silent.sparky.features.notifications.NotificationGroup

class NotificationGroupAdapter(
    val notificationsGroup: List<NotificationGroup>,
    val onSelectNotification: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationGroupAdapter.NotificationGroupViewHolder>() {


    inner class NotificationGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            val notificationGroup = notificationsGroup[bindingAdapterPosition]
            NotificationGroupLayoutBinding.bind(itemView).run {
                val highlightColor = Color.parseColor(notificationGroup.podcast.highLightColor)
                podcastTop.setupPodcast(notificationGroup.podcast)
                notificationRecycler.adapter =
                    NotificationItemAdapter(notificationGroup.notifications, highlightColor) {
                        onSelectNotification(it)
                    }
            }
        }

        private fun PodcastListLayoutBinding.setupPodcast(podcast: Podcast) {
            programName.text = podcast.name
            val highlightColor = Color.parseColor(podcast.highLightColor)
            Glide.with(root.context).load(podcast.iconURL).into(programIcon)
            programIcon.borderColor = highlightColor
            programIcon.fadeIn()
            root.fadeIn()
            root.setBackgroundColor(Color.TRANSPARENT)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationGroupViewHolder {
        return NotificationGroupViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_group_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotificationGroupViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = notificationsGroup.size

}