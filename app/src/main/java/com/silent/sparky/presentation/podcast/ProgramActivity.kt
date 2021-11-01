package com.silent.sparky.presentation.podcast

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.silent.core.data.model.podcast.PodcastHeader
import com.silent.core.data.podcast.Podcast
import com.silent.core.utils.WebUtils
import com.silent.ilustriscore.core.utilities.getView
import com.silent.ilustriscore.core.utilities.invisible
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.ilustriscore.core.utilities.visible
import com.silent.sparky.R
import com.silent.sparky.data.viewmodel.PodcastViewModel
import com.silent.sparky.presentation.podcast.adapter.HostAdapter
import com.silent.sparky.presentation.components.VideoHeaderAdapter
import kotlinx.android.synthetic.main.activity_program.*

class ProgramActivity : AppCompatActivity(R.layout.activity_program) {
    private val programViewModel = PodcastViewModel()
    val program by lazy { intent.getSerializableExtra("PROGRAM") as? Podcast }
    private val channelSectionsAdapter = VideoHeaderAdapter(ArrayList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
        setSupportActionBar(program_toolbar)
        program?.let {
            program_name.text = it.name
            Glide.with(this).load(it.iconURL).into(program_icon)
            programViewModel.getChannelData(it.youtubeID)
            programViewModel.checkChannelLive(it.youtubeID)
            channel_videos.adapter = channelSectionsAdapter
            if (it.hosts.isNotEmpty()) {
                hosts_title.visible()
                hosts_recycler_view.adapter = HostAdapter(it.hosts)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.program_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.twitch_link -> {
                program?.let {
                    WebUtils(this).openTwitch(it.twitch)
                }
                return false
            }
            R.id.twitter_link -> {
                program?.let {
                    WebUtils(this).openTwitter(it.twitter)
                }
                return false
            }
            R.id.instagram_link -> {
                program?.let {
                    WebUtils(this).openInstagram(it.instagram)
                }
               return false
            }
            else -> return false
        }
    }

    private fun observeViewModel() {
        programViewModel.podcastState.observe(this, {
            when(it) {
                is PodcastViewModel.PodcastState.PodcastDataRetrieved -> {
                    programViewModel.getChannelVideos(it.channelDetails.contentDetails.relatedPlaylists.uploads)
                }
                PodcastViewModel.PodcastState.PodcastFailedState -> {
                    getView().showSnackBar("Ocorreu um erro ao obter os vídeos")
                    error_view.fadeIn()
                    loading.fadeOut()
                }
                is PodcastViewModel.PodcastState.PodcastUploadsRetrieved -> {
                    channelSectionsAdapter.updateSection(
                        PodcastHeader("Últimos episódios", null,
                        it.videos,
                        it.playlistId,
                        RecyclerView.HORIZONTAL)
                    )
                    programViewModel.getChannelCuts(program!!.cuts)
                    loading.fadeOut()
                    channel_videos.fadeIn()
                }
                is PodcastViewModel.PodcastState.PodcastCutsRetrieved -> {
                    channelSectionsAdapter.updateSection(PodcastHeader("Últimos cortes", null,
                        it.videos,
                        it.playlistId,
                        RecyclerView.VERTICAL))
                }
                is PodcastViewModel.PodcastState.PodcastLiveState -> {
                    live_progress_bar.fadeIn()
                    program_icon.setOnClickListener { _ ->
                        WebUtils(this).openYoutubeVideo(it.videoID)
                    }
                }
                PodcastViewModel.PodcastState.PodcastOfflineState -> {
                    live_progress_bar.invisible()
                    program_icon.setOnClickListener {
                        WebUtils(this).openYoutubeChannel(program!!.youtubeID)
                    }
                }
            }
        })
    }

    companion object {
        private const val PROGRAM = "PROGRAM"
        fun getLaunchIntent(podcast: Podcast, context: Context) {
            Intent(context, ProgramActivity::class.java).apply {
                putExtra(PROGRAM, podcast)
                context.startActivity(this)
            }
        }
    }
}