package com.silent.sparky.manager.features

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.silent.sparky.manager.R
import kotlinx.android.synthetic.main.activity_new_podcast.*

class NewPodcastActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_podcast)
        val navController = findNavController(R.id.new_podcast_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.podcastGetYoutubeDataFragment, R.id.podcastGetHostsFragment
            )
        )
        new_podcast_toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    companion object {
        fun launchIntent(context: Context) {
            context.startActivity(Intent(context, NewPodcastActivity::class.java))
        }
    }
}