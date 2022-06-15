package com.silent.sparky.features.cuts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.ilustris.animations.fadeIn
import com.ilustris.animations.slideInBottom
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.silent.core.videos.Video
import com.silent.core.youtube.PlaylistResource
import com.silent.sparky.R
import com.silent.sparky.databinding.CutPlayerLayoutBinding

class CutsAdapter(val cuts: ArrayList<Video>) :
    RecyclerView.Adapter<CutsAdapter.CutViewHolder>() {

    fun updateCuts(newCuts: ArrayList<Video>) {
        cuts.addAll(newCuts)
        notifyDataSetChanged()
    }

    inner class CutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            CutPlayerLayoutBinding.bind(itemView).run {
                val cut = cuts[bindingAdapterPosition]
                videoTitle.text = cut.title
                cutPlayer.apply {
                    initialize(object : YouTubePlayerListener {
                        override fun onApiChange(youTubePlayer: YouTubePlayer) {
                        }

                        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                            videoProgress.setProgress(second.toInt(), true)
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
                            youTubePlayer.cueVideo(cut.youtubeID, 0f)
                            cutPlayer.setOnClickListener {
                                youTubePlayer.play()
                            }
                            videoCard.setOnClickListener {
                                youTubePlayer.play()
                            }
                        }

                        override fun onStateChange(
                            youTubePlayer: YouTubePlayer,
                            state: PlayerConstants.PlayerState
                        ) {
                            if (state == PlayerConstants.PlayerState.PLAYING) {
                                val color = MaterialColors.getColor(
                                    context,
                                    R.attr.colorAccent,
                                    ContextCompat.getColor(
                                        itemView.context,
                                        R.color.material_grey900
                                    )
                                )
                                videoCard.strokeColor = color
                                if (videoTitle.visibility == View.INVISIBLE) {
                                    videoTitle.fadeIn()
                                }
                                videoCard.setOnClickListener {
                                    youTubePlayer.pause()
                                }
                            } else {
                                val color =
                                    ContextCompat.getColor(
                                        itemView.context,
                                        R.color.material_grey900
                                    )
                                videoCard.strokeColor = color
                                videoCard.setOnClickListener {
                                    youTubePlayer.play()
                                }
                            }
                        }

                        override fun onVideoDuration(
                            youTubePlayer: YouTubePlayer,
                            duration: Float
                        ) {
                            videoProgress.max = duration.toInt()
                        }

                        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                        }

                        override fun onVideoLoadedFraction(
                            youTubePlayer: YouTubePlayer,
                            loadedFraction: Float
                        ) {
                        }
                    })

                }
                root.slideInBottom()
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