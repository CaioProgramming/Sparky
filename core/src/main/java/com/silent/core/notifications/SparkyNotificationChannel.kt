package com.silent.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import com.silent.core.R

object SparkyNotificationChannel {

    fun createChannel(context: Context): NotificationChannel {
        val channelId = context.getString(R.string.channel_id)
        val name = context.getString(R.string.channel_name)
        val description = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val sound =
            Uri.parse("android.resource://" + context.packageName + "/" + R.raw.notification)
        val notificationSoundUriAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val channel = NotificationChannel(channelId, name, importance).apply {
            setDescription(description)
            setSound(sound, notificationSoundUriAttributes)
        }
        return channel
    }

}