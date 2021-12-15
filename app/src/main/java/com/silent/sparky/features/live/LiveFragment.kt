package com.silent.sparky.features.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ilustris.animations.popOut
import com.ilustris.animations.slideInBottom
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.silent.core.podcast.Podcast
import com.silent.sparky.R
import com.silent.sparky.features.live.viewmodel.LiveViewModel
import com.silent.sparky.features.live.viewmodel.LiveViewState
import kotlinx.android.synthetic.main.podcast_live_fragment.*

class LiveFragment : Fragment() {

    private val args by navArgs<LiveFragmentArgs>()
    private val liveViewModel = LiveViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.podcast_live_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupView()
    }

    private fun setupView() {
        val live = args.liveObject
        live_title.text = live.title
        collapseButton.setOnClickListener {
            findNavController().popBackStack()
        }
        loadVideo(live.feed.youtube)
        liveViewModel.getPodcastInfo(live.feed.youtube)
    }

    private fun observeViewModel() {
        liveViewModel.liveViewState.observe(this, {
            when (it) {
                is LiveViewState.PodcastData -> {
                    setupPodcast(it.podcast)
                }
            }
        })
    }

    private fun setupPodcast(podcast: Podcast) {
        channel_name.text = podcast.name
        channel_name.setOnClickListener {
            val bundle = bundleOf("podcast_id" to podcast.id)
            findNavController().navigate(R.id.action_liveFragment_to_podcastFragment, bundle)
        }
        live_top.slideInBottom()
    }


    fun loadVideo(videoID: String) {
        live_player.initialize(object : YouTubePlayerListener {
            override fun onApiChange(youTubePlayer: YouTubePlayer) {

            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
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
                youTubePlayer.loadVideo(videoID, 0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                if (state == PlayerConstants.PlayerState.PLAYING) {
                    if (loading.isVisible) {
                        loading.pauseAnimation()
                        loading.popOut()
                    }
                }
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {

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