package com.silent.core.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.silent.core.BuildConfig
import com.silent.core.R
import com.silent.core.preferences.PreferencesService
import com.silent.core.utils.HOME_ACT
import com.silent.core.utils.ImageUtils
import com.silent.core.utils.TOKEN_PREFERENCES
import com.silent.core.utils.toAlphabetInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject


class SparkyMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        handleNotification(message)

    }

    private fun handleNotification(message: RemoteMessage) {
        message.notification?.let { notification ->
            val icon = ImageUtils.getNotificationIcon(notification.icon)
            var defaultColor = baseContext.getColor(R.color.material_yellow700)
            if (BuildConfig.DEBUG) Log.i(
                "SPARKY MESSAGING SERVICE",
                "Received metadata ${message.data}"
            )
            var podcast: JSONObject? = null
            var video: JSONObject? = null
            if (message.data.containsKey("podcast")) {
                val podcastMap = message.data["podcast"]
                Log.i("SPARKY MESSAGING SERVICE", "podcast data founded -> $podcastMap")
                Log.i("SPARKY MESSAGING SERVICE", "parsing json data -> ${podcastMap.toString()}")
                podcast = podcastMap?.let { JSONObject(it) }
            }
            if (message.data.containsKey("video")) {
                val videoMap = message.data["video"]
                Log.i("SPARKY MESSAGING SERVICE", "video data founded -> $videoMap")
                Log.i("SPARKY MESSAGING SERVICE", "parsing json data -> ${videoMap.toString()}")
                video = videoMap?.let { JSONObject(it) }
            }
            notification.color?.let {
                defaultColor = Color.parseColor(it)
            }
            val homeIntent = Intent(this, Class.forName(HOME_ACT)).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                podcast?.let {
                    putExtra("podcast", podcast.toString())
                }
                video?.let {
                    putExtra("video", video.toString())
                }
            }
            val notificationId = podcast?.getString("name")?.toAlphabetInt() ?: 0
            val flag =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_CANCEL_CURRENT
            val pendingIntent = PendingIntent.getActivity(this, 0, homeIntent, flag)
            val channelId = getString(R.string.channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(icon.drawable)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(defaultColor)
                .setColorized(true)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notificationBuilder.build())
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val preferencesService = PreferencesService(this)
        runBlocking {
            withContext(Dispatchers.IO) {
                preferencesService.editPreference(TOKEN_PREFERENCES, token)
            }
        }
    }

}