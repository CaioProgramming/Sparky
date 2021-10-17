package com.silent.sparky.program

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.silent.core.program.Program
import com.silent.core.youtube.PlaylistResource
import com.silent.ilustriscore.core.utilities.getView
import com.silent.ilustriscore.core.utilities.gone
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.ilustriscore.core.utilities.visible
import com.silent.sparky.R
import kotlinx.android.synthetic.main.activity_program.*

class ProgramActivity : AppCompatActivity(R.layout.activity_program) {
    val programViewModel = ProgramViewModel()
    val program by lazy { intent.getSerializableExtra("PROGRAM") as? Program }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
        program?.let {
            program_name.text = it.name
            Glide.with(this).load(it.iconURL).into(program_icon)
            programViewModel.getChannelData(it)
        }
    }

    private fun observeViewModel() {
        programViewModel.channelState.observe(this, {
            when(it) {
                is ProgramViewModel.ChannelState.ChannelDataRetrieved -> {
                    program_description.text = it.channelDetails.snippet.description
                    programViewModel.getChannelVideos(it.channelDetails.contentDetails.relatedPlaylists.uploads)
                }
                ProgramViewModel.ChannelState.ChannelFailedState -> {
                    getView().showSnackBar("Ocorreu um erro ao obter os vídeos")
                    error_view.visible()
                }
                is ProgramViewModel.ChannelState.ChannelUploadsRetrieved -> {
                    setupVideoRecycler(it.videos)
                    getView().showSnackBar("Playlists obtidas")
                }
            }
        })
    }

    private fun setupVideoRecycler(videos: List<PlaylistResource>) {
        lastest_videos.adapter = VideosAdapter(videos)
        loading.gone()
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