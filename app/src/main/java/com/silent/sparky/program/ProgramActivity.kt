package com.silent.sparky.program

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
import com.silent.core.program.Program
import com.silent.core.utils.WebUtils
import com.silent.ilustriscore.core.utilities.getView
import com.silent.ilustriscore.core.utilities.gone
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.ilustriscore.core.utilities.visible
import com.silent.sparky.R
import com.silent.sparky.program.adapter.HostAdapter
import com.silent.sparky.program.adapter.VideoHeaderAdapter
import com.silent.sparky.program.data.ProgramHeader
import kotlinx.android.synthetic.main.activity_program.*

class ProgramActivity : AppCompatActivity(R.layout.activity_program) {
    private val programViewModel = ProgramViewModel()
    val program by lazy { intent.getSerializableExtra("PROGRAM") as? Program }
    private val channelSectionsAdapter = VideoHeaderAdapter(ArrayList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
        setSupportActionBar(program_toolbar)
        program?.let {
            program_name.text = it.name
            Glide.with(this).load(it.iconURL).into(program_icon)
            program_icon.setOnLongClickListener {  _ ->
                WebUtils(this).openYoutubeChannel(it.youtubeID)
                false
            }
            programViewModel.getChannelData(it)
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
        programViewModel.channelState.observe(this, {
            when(it) {
                is ProgramViewModel.ChannelState.ChannelDataRetrieved -> {
                    programViewModel.getChannelVideos(it.channelDetails.contentDetails.relatedPlaylists.uploads)
                }
                ProgramViewModel.ChannelState.ChannelFailedState -> {
                    getView().showSnackBar("Ocorreu um erro ao obter os vídeos")
                    error_view.fadeIn()
                    loading.fadeOut()
                }
                is ProgramViewModel.ChannelState.ChannelUploadsRetrieved -> {
                    channelSectionsAdapter.updateSection(ProgramHeader("Últimos episódios",
                        it.videos,
                        it.playlistId,
                        RecyclerView.HORIZONTAL))
                    programViewModel.getChannelCuts(program!!.cuts)
                    loading.fadeOut()
                    channel_videos.fadeIn()
                }
                is ProgramViewModel.ChannelState.ChannelCutsRetrieved -> {
                    channelSectionsAdapter.updateSection(ProgramHeader("Últimos cortes",
                        it.videos,
                        it.playlistId,
                        RecyclerView.VERTICAL))
                }
            }
        })
    }

    companion object {
        private const val PROGRAM = "PROGRAM"
        fun getLaunchIntent(program: Program, context: Context) {
            Intent(context, ProgramActivity::class.java).apply {
                putExtra(PROGRAM, program)
                context.startActivity(this)
            }
        }
    }
}