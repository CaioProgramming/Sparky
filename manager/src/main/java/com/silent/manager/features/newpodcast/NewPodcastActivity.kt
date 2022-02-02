package com.silent.manager.features.newpodcast

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.silent.manager.R
import com.silent.manager.databinding.ActivityNewPodcastBinding
import com.silent.manager.states.NewPodcastState

class NewPodcastActivity : AppCompatActivity() {

    private val newPodcastViewModel = NewPodcastViewModel(application)
    private var newPodcastBinding: ActivityNewPodcastBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newPodcastBinding = ActivityNewPodcastBinding.inflate(layoutInflater)
        setContentView(newPodcastBinding!!.root)

        newPodcastBinding?.run {
            val navController = findNavController(R.id.newpodcast_navhost)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.podcastGetYoutubeDataFragment, R.id.podcastGetHostsFragment
                )
            )
            observeViewModel()
            newPodcastToolbar.setupWithNavController(navController, appBarConfiguration)
        }

    }

    private fun observeViewModel() {
        newPodcastViewModel.newPodcastState.observe(this, {
            when (it) {
                NewPodcastState.InvalidPodcast -> {

                }

                NewPodcastState.PodcastUpdated -> {
                    newPodcastBinding?.newPodcastProgress?.setProgress(2, true)
                }
                else -> {
                    //DO NOTHING
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