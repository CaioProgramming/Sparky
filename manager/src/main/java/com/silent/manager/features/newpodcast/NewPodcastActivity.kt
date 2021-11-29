package com.silent.manager.features.newpodcast

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.silent.ilustriscore.core.utilities.gone
import com.silent.ilustriscore.core.utilities.visible
import com.silent.manager.R
import com.silent.manager.states.NewPodcastState
import kotlinx.android.synthetic.main.activity_new_podcast.*
import kotlinx.android.synthetic.main.podcast_dialog.*

class NewPodcastActivity : AppCompatActivity() {

    private val newPodcastViewModel = NewPodcastViewModel()

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
        observeViewModel()
        new_podcast_toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun observeViewModel() {
        newPodcastViewModel.newPodcastState.observe(this, {
            when (it) {
                NewPodcastState.InvalidPodcast -> {
                    continue_button.gone()
                }
                NewPodcastState.PodcastUpdated -> {
                    continue_button.visible()
                    continue_button.setOnClickListener {
                        findNavController(R.id.new_podcast_fragment).navigate(R.id.action_podcastGetYoutubeFormFragment_to_GetHostsFragment)
                    }
                }

                else -> {

                }
            }
        })
    }

    companion object {
        fun launchIntent(context: Context) {
            context.startActivity(Intent(context, NewPodcastActivity::class.java))
        }
    }
}