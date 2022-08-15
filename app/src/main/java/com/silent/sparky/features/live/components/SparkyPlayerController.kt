package com.silent.sparky.features.live.components

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.ui.extensions.gone
import com.ilustris.ui.extensions.invisible
import com.ilustris.ui.extensions.visible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.sparky.R
import com.silent.sparky.databinding.CustomPlayerLayoutBinding

class SparkyPlayerController(
    playerUI: View,
    private val youTubePlayerView: YouTubePlayerView,
    val youTubePlayer: YouTubePlayer,
    private val highlightColor: Int,
    private val podcastIcon: Int = R.drawable.ic_iconmonstr_connection_1,
    private val videoId: String,
    private val isLive: Boolean
) : AbstractYouTubePlayerListener() {
    private var isPlaying = false
    private val binding = CustomPlayerLayoutBinding.bind(playerUI.findViewById(R.id.player_root))
    var videoDuration = 0f

    init {
        binding.setupView()
    }

    private fun CustomPlayerLayoutBinding.setupView() {
        if (isLive) {
            playerSeekbar.invisible()
        } else {
            playerSeekbar.visible()
        }
        root.setOnClickListener {
            playerBottom.fadeIn()
            hideBottom()
        }
        playerBottom.gone()
        playPauseButton.setOnClickListener {
            if (isPlaying) youTubePlayer.pause() else youTubePlayer.play()
        }
        playerSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    videoTime.text = "${getDurationString(progress.toFloat())} / ${
                        getDurationString(videoDuration)
                    }"
                    youTubePlayer.seekTo(progress.toFloat())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    videoTime.text = "${getDurationString(it.progress.toFloat())} / ${
                        getDurationString(videoDuration)
                    }"
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
        playerSeekbar.thumbTintList = ColorStateList.valueOf(highlightColor)
        playerSeekbar.progressTintList = ColorStateList.valueOf(highlightColor)
        playerSeekbar.thumb = root.context.getDrawable(podcastIcon)
        youTubePlayerView.enterFullScreen()
        youTubePlayer.loadVideo(videoId, 0f)
    }

    private fun CustomPlayerLayoutBinding.showLoading() {
        playerBottom.fadeOut()
        playerLoading.playAnimation()
        playerLoading.fadeIn()
        errorMessage.gone()
        root.setBackgroundColor(Color.BLACK)
    }


    private fun CustomPlayerLayoutBinding.stopLoading() {
        playerLoading.cancelAnimation()
        playerLoading.fadeOut()
        root.setBackgroundResource(R.drawable.faded_gradient)
        videoTime.visible()
        playPauseButton.visible()
        hideBottom()
    }


    override fun onReady(youTubePlayer: YouTubePlayer) {
        super.onReady(youTubePlayer)
        binding.stopLoading()
        val params = youTubePlayerView.layoutParams
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        youTubePlayerView.layoutParams = params
        youTubePlayerView.requestLayout()
        binding.playerBottom.fadeIn()
    }

    private fun CustomPlayerLayoutBinding.hideBottom() {
        delayedFunction(10000) {
            if (isPlaying) {
                playerBottom.fadeOut()
            }
        }
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
                isPlaying = true
                binding.stopLoading()
                binding.playPauseButton.setImageResource(R.drawable.zzz_pause)
            }
            PlayerConstants.PlayerState.PAUSED -> {
                isPlaying = false
                binding.playPauseButton.setImageResource(R.drawable.zzz_play)
            }
            PlayerConstants.PlayerState.BUFFERING -> {
                binding.showLoading()
            }
            PlayerConstants.PlayerState.VIDEO_CUED -> {
                binding.stopLoading()
                binding.playerBottom.fadeIn()
                binding.hideBottom()
            }
        }
    }

    private fun getDurationString(seconds: Float): String {

        return DateUtils.formatElapsedTime(seconds.toLong())
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        super.onCurrentSecond(youTubePlayer, second)
        binding.videoTime.text =
            "${getDurationString(second)} / ${getDurationString(videoDuration)}"
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