package com.silent.sparky.presentation.cuts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.color.MaterialColors
import com.ilustris.animations.fadeIn
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.silent.core.youtube.PlaylistResource
import com.silent.sparky.R
import com.silent.sparky.databinding.CutPlayerLayoutBinding

class CutsAdapter(val cuts: ArrayList<PlaylistResource>): PagerAdapter() {

    override fun getCount() = cuts.size
    var player: YouTubePlayerView? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.cut_player_layout, container, false)
        CutPlayerLayoutBinding.bind(view).run {
            val cut = cuts[position]
            videoTitle.text = cut.snippet.title
            player = cutPlayer
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
                        youTubePlayer.cueVideo(cut.snippet.resourceId.videoId,0f)
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
                            val color = MaterialColors.getColor(context, R.attr.colorAccent, ContextCompat.getColor(view.context, R.color.material_grey900))
                            videoCard.strokeColor = color
                            if (videoTitle.visibility == View.INVISIBLE) {
                                videoTitle.fadeIn()
                            }
                            videoCard.setOnClickListener {
                                youTubePlayer.pause()
                            }
                        } else {
                            val color = ContextCompat.getColor(view.context, R.color.material_grey900)
                            videoCard.strokeColor = color
                            videoCard.setOnClickListener {
                                youTubePlayer.play()
                            }
                        }
                    }

                    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
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
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    fun updateCuts(cuts: ArrayList<PlaylistResource>) {
        cuts.addAll(cuts)
        notifyDataSetChanged()
    }

}