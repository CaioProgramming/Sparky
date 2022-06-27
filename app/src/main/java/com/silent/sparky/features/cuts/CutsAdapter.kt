package com.silent.sparky.features.cuts

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ilustris.animations.fadeIn
import com.ilustris.ui.extensions.gone
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.silent.core.podcast.Podcast
import com.silent.core.videos.Video
import com.silent.sparky.R
import com.silent.sparky.databinding.CutPlayerLayoutBinding

class CutsAdapter(
    val cuts: ArrayList<Video>,
    private val selectPodcast: (Podcast) -> Unit
) :
    RecyclerView.Adapter<CutsAdapter.CutViewHolder>() {

    fun updateCuts(newCuts: ArrayList<Video>) {
        cuts.addAll(newCuts)
        notifyDataSetChanged()
    }

    inner class CutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            CutPlayerLayoutBinding.bind(itemView).run {
                try {
                    val cut = cuts[bindingAdapterPosition]
                    cutTitle.text = cut.title
                    Glide.with(itemView).load(cut.thumbnailUrl).error(cut.podcast?.iconURL).into(videoThumb)
                    cut.podcast?.let {
                        val highlightColor = Color.parseColor(it.highLightColor)
                        videoProgress.progressTintList = ColorStateList.valueOf(highlightColor)
                        Glide.with(itemView.context)
                            .load(it.iconURL)
                            .into(podcastIcon)
                        podcastIcon.setOnClickListener { _ ->
                            selectPodcast(it)
                        }
                        podcastIcon.borderColor = highlightColor
                    }
                    videoCard.setOnClickListener {
                        videoProgress.isIndeterminate = true
                        videoProgress.fadeIn()

                    }
                    cutPlayer.initialize(cutPlayerListener(cut.youtubeID, videoProgress, videoCard) { state ->
                        if (state == PlayerConstants.PlayerState.PLAYING) {
                            cutPlayer.fadeIn()
                            videoThumb.gone()
                        }
                    })

                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, "\n \nError loading video ${e.message}")
                    e.printStackTrace()
                }
            }

        }

        private fun cutPlayerListener(youtubeId: String, progressBar: ProgressBar, playView: View, playerStateChange: (PlayerConstants.PlayerState) -> Unit) : YouTubePlayerListener {
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
                    playView.setOnClickListener {
                        youTubePlayer.play()
                    }
                    //cutPlayer.fadeIn()
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    playerStateChange(state)
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