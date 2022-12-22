package com.silent.sparky.features.notifications.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilustris.ui.extensions.gone
import com.silent.core.notifications.Notification
import com.silent.ilustriscore.core.utilities.formatDate
import com.silent.sparky.R
import com.silent.sparky.databinding.NotificationLayoutBinding

class NotificationItemAdapter(
    val notifications: List<Notification>,
    val highlightColor: Int,
    val onSelectNotification: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationItemAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData() {
            val notification = notifications[bindingAdapterPosition]
            NotificationLayoutBinding.bind(itemView).run {
                notificationIcon.setImageResource(notification.getNotificationType().icon)
                notificationIcon.imageTintList = ColorStateList.valueOf(highlightColor)
                notificationTitle.text = notification.title
                notificationMessage.text = notification.message
                notificationDate.text = notification.sent_at.formatDate("EEE, dd MMM")
                itemView.setOnClickListener {
                    onSelectNotification(notification)
                }
                videoThumb.root.gone()
                /*notification.video?.let {
                    videoThumb.root.visible()
                    Glide.with(itemView.context).load(it.thumbnailUrl).into(videoThumb.videoThumb)
                }*/
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.notification_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bindData()
    }

    override fun getItemCount(): Int = notifications.size

}