package com.silent.sparky.features.cuts.ui.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.ui.extensions.setAlpha
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.silent.core.utils.ImageUtils
import com.silent.core.videos.Video
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.sparky.R
import com.silent.sparky.databinding.CutPlayerLayoutBinding

class CutsAdapter(val cuts: ArrayList<Video>,private val moveToNext: () -> Unit) :
    RecyclerView.Adapter<CutsAdapter.CutViewHolder>() {

    fun initializeCut(position: Int) {
        Log.i(javaClass.simpleName, "initializeCut: updating playing  to $position")
        initializedCut = position
        delayedFunction(2000) {
            notifyItemChanged(position)
        }
    }

    var initializedCut: Int? = null
    var enabled: Boolean = true

    inner class CutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var youTubePlayerListener: YouTubePlayerListener? = null
        fun bind() {
            CutPlayerLayoutBinding.bind(itemView).run {
                try {
                    val playingVideo = bindingAdapterPosition == initializedCut && enabled
                    val cut = cuts[bindingAdapterPosition]
                    cutTitle.text = cut.title
                    cut.podcast?.let {
                        val highlightColor = Color.parseColor(it.highLightColor)
                        videoProgress.setIndicatorColor(highlightColor)
                        videoProgress.trackColor = highlightColor.setAlpha(0.4f)
                    }
                    cutPlayer.enableBackgroundPlayback(false)
                    if (youTubePlayerListener == null && playingVideo) {
                        youTubePlayerListener = cutPlayerListener(cut.youtubeID, videoProgress) { state, player ->
                            if (state == PlayerConstants.PlayerState.PLAYING && cutThumbnail.isVisible) {
                                cutThumbnail.fadeOut()
                                cutPlayer.fadeIn()
                            } else if (state == PlayerConstants.PlayerState.ENDED) {
                                moveToNext()
                            }
                        }
                        cutPlayer.initialize(youTubePlayerListener!!)
                    } else {
                        cutPlayer.removeYouTubePlayerListener(youTubePlayerListener!!)
                        youTubePlayerListener = null
                    }
                    Glide.with(itemView).load(ImageUtils.getYoutubeThumb(cut.youtubeID)).placeholder(R.drawable.ic_iconmonstr_connection_1).error(cut.podcast?.iconURL).into(cutThumbnail)
                    cutTitle.fadeIn()
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
                    youTubePlayer.loadVideo(youtubeId, 0f)
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
                    progressBar.fadeIn()
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