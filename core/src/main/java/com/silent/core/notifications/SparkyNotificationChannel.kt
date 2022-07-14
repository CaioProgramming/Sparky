package com.silent.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.silent.core.R

object SparkyNotificationChannel {

    fun createChannel(context: Context): NotificationChannel {
        val channelId = context.getString(R.string.channel_id)
       val name = context.getString(R.string.channel_name)
       val description = context.getString(R.string.channel_description)
       val importance = NotificationManager.IMPORTANCE_DEFAULT
       val channel = NotificationChannel(channelId, name, importance)
       channel.description = description
       return channel
   }

}