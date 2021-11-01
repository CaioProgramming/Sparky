package com.silent.newpodcasts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
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
}