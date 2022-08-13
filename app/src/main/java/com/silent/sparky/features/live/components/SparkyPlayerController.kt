package com.silent.sparky.features.live.components

import android.content.res.ColorStateList
import android.view.View
import android.widget.SeekBar
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.ui.extensions.gone
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.silent.sparky.R
import com.silent.sparky.databinding.CustomPlayerLayoutBinding

class SparkyPlayerController(
    playerUI: View,
    val youTubePlayerView: YouTubePlayerView,
    val youTubePlayer: YouTubePlayer,
    private val highlightColor: Int,
    private val podcastIcon: Int = R.drawable.ic_iconmonstr_connection_1,
    private val videoId: String
) : AbstractYouTubePlayerListener() {
    private var isPlaying = false
    private val binding = CustomPlayerLayoutBinding.bind(playerUI.findViewById(R.id.player_root))
    var videoDuration = 0f

    init {
        binding.setupView()
    }

    private fun CustomPlayerLayoutBinding.setupView() {

        playPauseButton.setOnClickListener {
            if (isPlaying) youTubePlayer.pause() else youTubePlayer.play()
            isPlaying = !isPlaying
        }
        playerLoading.setColorFilter(highlightColor)
        playerSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    youTubePlayer.seekTo(progress.toFloat())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
        playerSeekbar.thumbTintList = ColorStateList.valueOf(highlightColor)
        playerSeekbar.progressTintList = ColorStateList.valueOf(highlightColor)
        playerSeekbar.thumb = root.context.getDrawable(podcastIcon)
        youTubePlayer.loadVideo(videoId, 0f)
    }

    private fun CustomPlayerLayoutBinding.showLoading() {
        playerLoading.playAnimation()
        playerLoading.fadeIn()
        errorMessage.gone()
    }


    private fun CustomPlayerLayoutBinding.stopLoading() {
        playerLoading.cancelAnimation()
        playerLoading.fadeOut()
    }


    override fun onReady(youTubePlayer: YouTubePlayer) {
        super.onReady(youTubePlayer)
        binding.stopLoading()
        youTubePlayerView.enterFullScreen()
        binding.videoTime.fadeIn()
    }


    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        super.onStateChange(youTubePlayer, state)
        when (state) {
            PlayerConstants.PlayerState.UNKNOWN -> {
                binding.stopLoading()
            }
            PlayerConstants.PlayerState.UNSTARTED,
            PlayerConstants.PlayerState.ENDED -> {
                binding.showLoading()
            }
            PlayerConstants.PlayerState.PLAYING -> {
                binding.stopLoading()
                binding.playPauseButton.setImageResource(R.drawable.zzz_pause)
                binding.playPauseButton.fadeOut()
                binding.seeOnYoutube.fadeOut()
            }
            PlayerConstants.PlayerState.PAUSED -> {
                binding.playPauseButton.setImageResource(R.drawable.zzz_play)
                binding.playPauseButton.fadeIn()
                binding.seeOnYoutube.fadeIn()
            }
            PlayerConstants.PlayerState.BUFFERING -> {
                binding.showLoading()
            }
            PlayerConstants.PlayerState.VIDEO_CUED -> {
                binding.stopLoading()
            }
        }
    }


    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        super.onCurrentSecond(youTubePlayer, second)
        binding.videoTime.text = "$second / $videoDuration"
        binding.playerSeekbar.setProgress(second.toInt(), true)
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        super.onVideoDuration(youTubePlayer, duration)
        videoDuration = duration
        binding.playerSeekbar.max = duration.toInt()
    }

    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
        super.onError(youTubePlayer, error)
        binding.run {
            playPauseButton.gone()
            errorMessage.text = "Ocorreu um erro ao reproduzir esse vídeo(${error.name}"
            errorMessage.fadeIn()
            stopLoading()
        }

    }


}