package com.silent.sparky.features.cuts.ui.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.ui.extensions.setAlpha
import com.ilustris.ui.extensions.visible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.silent.core.videos.Video
import com.silent.sparky.R
import com.silent.sparky.databinding.CutPlayerLayoutBinding

class CutsAdapter(val cuts: ArrayList<Video>) :
    RecyclerView.Adapter<CutsAdapter.CutViewHolder>() {

    fun initializeCut(position: Int) {
        Log.i(javaClass.simpleName, "initializeCut: updating playing  to $position")
        initializedCut = position
    }

    var initializedCut: Int? = null
    private var playerInitialized: Boolean = false

    inner class CutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            CutPlayerLayoutBinding.bind(itemView).run {
                try {
                    val cut = cuts[bindingAdapterPosition]
                    cutTitle.text = cut.title
                    cut.podcast?.let {
                        val highlightColor = Color.parseColor(it.highLightColor)
                        videoProgress.setIndicatorColor(highlightColor)
                        videoProgress.trackColor = highlightColor.setAlpha(0.4f)
                    }
                    cutPlayer.enableBackgroundPlayback(false)
                    if (!playerInitialized) {
                        cutPlayer.initialize(
                            cutPlayerListener(
                                cut.youtubeID,
                                videoProgress
                            ) { state, player ->
                                if (state == PlayerConstants.PlayerState.VIDEO_CUED) {
                                    playerInitialized = true
                                    initializedCut?.let {
                                        if (bindingAdapterPosition == initializedCut) {
                                            player.play()
                                            cutThumbnail.fadeOut()
                                            cutPlayer.fadeIn()
                                        }
                                    }
                                }
                            })
                    }
                    Glide.with(itemView).load(cut.thumbnailUrl).error(cut.podcast?.iconURL)
                        .into(cutThumbnail)

                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, "\n \nError loading video ${e.message}")
                    e.printStackTrace()
                }
            }

        }

        private fun cutPlayerListener(
            youtubeId: String,
            progressBar: ProgressBar,
            playerStateChange: (PlayerConstants.PlayerState, YouTubePlayer) -> Unit
        ): YouTubePlayerListener {
            return object : YouTubePlayerListener {
                override fun onApiChange(youTubePlayer: YouTubePlayer) {
                }

                override fun onCurrentSecond(
                    youTubePlayer: YouTubePlayer,
                    second: Float
                ) {
                    progressBar.setProgress(second.toInt(), true)
                }

                override fun onError(
                    youTubePlayer: YouTubePlayer,
                    error: PlayerConstants.PlayerError
                ) {
                }

                override fun onPlaybackQualityChange(
                    youTubePlayer: YouTubePlayer,
                    playbackQuality: PlayerConstants.PlaybackQuality
                ) {
                }

                override fun onPlaybackRateChange(
                    youTubePlayer: YouTubePlayer,
                    playbackRate: PlayerConstants.PlaybackRate
                ) {
                }

                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(youtubeId, 0f)
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    playerStateChange(state, youTubePlayer)
                }

                override fun onVideoDuration(
                    youTubePlayer: YouTubePlayer,
                    duration: Float
                ) {
                    progressBar.isIndeterminate = false
                    progressBar.max = duration.toInt()
                }

                override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                }

                override fun onVideoLoadedFraction(
                    youTubePlayer: YouTubePlayer,
                    loadedFraction: Float
                ) {
                }
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CutViewHolder {
        return CutViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cut_player_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CutViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = cuts.size
}