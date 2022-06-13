package com.silent.sparky.features.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.silent.sparky.R
import com.silent.sparky.databinding.PodcastLiveFragmentBinding

class LiveFragment : Fragment() {

    private var podcastLiveFragmentBinding: PodcastLiveFragmentBinding? = null
    private val args by navArgs<LiveFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        podcastLiveFragmentBinding = PodcastLiveFragmentBinding.inflate(inflater)
        return podcastLiveFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        podcastLiveFragmentBinding = PodcastLiveFragmentBinding.bind(view)
        setupView()
    }

    private fun setupView() {
        val live = args.liveObject
        loadVideo(live.videoID)
        podcastLiveFragmentBinding?.run {
            liveTitle.text = live.title
            channelName.text = live.podcast.name
            channelName.setOnClickListener {
                val bundle = bundleOf("podcast_id" to live.podcast.id)
                findNavController().navigate(R.id.action_liveFragment_to_podcastFragment, bundle)
            }
            collapseButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }

    }

    fun loadVideo(videoID: String) {
        podcastLiveFragmentBinding?.livePlayer?.initialize(object : YouTubePlayerListener {
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