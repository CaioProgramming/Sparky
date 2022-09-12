package com.silent.sparky.features.live.components

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.facebook.shimmer.Shimmer.ColorHighlightBuilder
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
    private val isLive: Boolean,
    private val requestFullScreen: (Boolean) -> Unit
) : AbstractYouTubePlayerListener() {
    private var isPlaying = false
    private var isFullScreen = false
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
            if (!playerBottom.isVisible) {
                showUI()
            }
            hideUI()
        }
        playPauseButton.setOnClickListener {
            if (isPlaying) youTubePlayer.pause() else youTubePlayer.play()
        }
        enterFullScreen.setOnClickListener {
            isFullScreen = !isFullScreen
            requestFullScreen(isFullScreen)
            val icon =
                if (isFullScreen) R.drawable.ic_iconmonstr_exit_fullscreen_11 else R.drawable.ic_iconmonstr_enter_fullscreen_10
            val description =
                if (isFullScreen) R.string.enter_full_screen_description else R.string.exit_full_screen_description
            enterFullScreen.setImageResource(icon)
            enterFullScreen.contentDescription = root.context.getString(description)
            root.layoutParams = root.layoutParams.apply {
                val newWidth =
                    if (isFullScreen) ViewGroup.LayoutParams.MATCH_PARENT else root.context.resources.getDimensionPixelOffset(
                        R.dimen.player_card_portrait_width
                    )
                width = newWidth
            }
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
        podcastNotIcon.setImageResource(podcastIcon)
        val shimmerBuilder = ColorHighlightBuilder()
            .setBaseColor(ContextCompat.getColor(root.context, R.color.md_grey900))
            .setHighlightColor(highlightColor)
            .setDuration(2500)
            .setIntensity(0.9f)
            .setDropoff(0.9f)
            .setBaseAlpha(0.5f)
            .setHighlightAlpha(1f)
        val shimmer = shimmerBuilder.build()
        playerLoading.setShimmer(shimmer)
        youTubePlayerView.enterFullScreen()
        youTubePlayer.loadVideo(videoId, 0f)
    }

    private fun CustomPlayerLayoutBinding.showLoading() {
        playerBottom.fadeOut()
        playerLoading.visible()
        playerLoading.showShimmer(true)
        errorMessage.gone()
        root.setBackgroundColor(Color.BLACK)
    }


    private fun CustomPlayerLayoutBinding.stopLoading() {
        playerLoading.stopShimmer()
        playerLoading.hideShimmer()
        playerLoading.fadeOut()
        root.setBackgroundResource(R.drawable.faded_gradient)
        videoTime.visible()
        playPauseButton.visible()
        hideUI()
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

    private fun CustomPlayerLayoutBinding.hideUI() {
        delayedFunction(10000) {
            if (isPlaying) {
                playerBottom.fadeOut()
            }
        }
    }

    private fun CustomPlayerLayoutBinding.showUI() {
        playerBottom.fadeIn()
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
                binding.run {
                    stopLoading()
                    playPauseButton.setImageResource(R.drawable.zzz_pause)
                    playPauseButton.contentDescription =
                        root.context.getString(R.string.pause_button)
                }


            }
            PlayerConstants.PlayerState.PAUSED -> {
                isPlaying = false
                binding.playPauseButton.setImageResource(R.drawable.zzz_play)
                binding.playPauseButton.contentDescription =
                    binding.root.context.getString(R.string.play_button)
            }
            PlayerConstants.PlayerState.BUFFERING -> {
                binding.showLoading()
            }
            PlayerConstants.PlayerState.VIDEO_CUED -> {
                binding.stopLoading()
                binding.playerBottom.fadeIn()
                binding.hideUI()
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