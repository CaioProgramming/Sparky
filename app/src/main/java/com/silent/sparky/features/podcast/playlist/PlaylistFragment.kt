package com.silent.sparky.features.podcast.playlist

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ilustris.animations.fadeIn
import com.silent.core.podcast.HeaderType
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastHeader
import com.silent.core.videos.Video
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentPlaylistBinding
import com.silent.sparky.features.live.data.LiveHeader
import com.silent.sparky.features.live.data.VideoMedia
import com.silent.sparky.features.podcast.adapter.VideosAdapter

class PlaylistFragment: Fragment() {

    private val args by navArgs<PlaylistFragmentArgs>()
    private var playlistFragmentBinding: FragmentPlaylistBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        playlistFragmentBinding = FragmentPlaylistBinding.inflate(layoutInflater)
        return playlistFragmentBinding?.root
    }

    private fun navigateToLive(podcast: Podcast, video: Video) {
        val type =
            if (args.header.title!!.contains("Cortes")) VideoMedia.CUT else VideoMedia.EPISODE
        val liveHeader = LiveHeader(podcast, video.title, video.description, video.youtubeID, type)
        val bundle = bundleOf("live_object" to liveHeader)
        findNavController().navigate(R.id.action_playlistFragment_to_liveFragment, bundle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistFragmentBinding?.setupHeader(args.header)


    }

    private fun FragmentPlaylistBinding.setupHeader(header: PodcastHeader) {
        if (header.type == HeaderType.VIDEOS) {
            title.text = header.subTitle
            subtitle.text = "${header.title} - ${header.videos!!.size} resultados"
            loading.setIndicatorColor(Color.parseColor(header.highLightColor))
            videosRecycler.adapter =
                VideosAdapter(header.videos!!, header.highLightColor) { video ->
                    header.podcast?.let {
                        navigateToLive(it, video)
                    }
                }
            loading.setProgress(100, true)
            videosRecycler.fadeIn()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playlistFragmentBinding = null
    }
}