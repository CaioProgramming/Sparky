package com.silent.sparky.features

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.silent.core.podcast.Podcast
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.sparky.R
import com.silent.sparky.features.home.HomeActivity

class NotificationActivity : AppCompatActivity() {

    private val podcastExtra: String? by lazy {
        intent.extras?.getString("podcast")
    }
    private val videoExtra: String? by lazy {
        intent.extras?.getString("video")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        parseNotification(::redirectNotification)
    }

    private fun parseNotification(parseCallback: (Podcast?) -> Unit) {
        Log.i(
            javaClass.simpleName,
            "validatePush: \n podcastObject -> ${podcastExtra}\nVideoObject -> $videoExtra"
        )
        val podcast: Podcast? = podcastExtra?.let { Gson().fromJson(it, Podcast::class.java) }
        parseCallback(podcast)
    }

    private fun redirectNotification(podcast: Podcast?) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("podcast", podcast)
            putExtra("video", videoExtra)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        delayedFunction(1500) {
            startActivity(homeIntent)
        }
    }
}