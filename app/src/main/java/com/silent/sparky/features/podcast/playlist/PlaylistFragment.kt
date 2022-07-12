package com.silent.sparky.features.podcast.playlist

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ilustris.animations.fadeIn
import com.silent.sparky.databinding.FragmentPlaylistBinding
import com.silent.sparky.databinding.FragmentPodcastBinding
import com.silent.sparky.features.home.data.PodcastHeader
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistFragmentBinding?.setupHeader(args.header)


    }

    private fun FragmentPlaylistBinding.setupHeader(header: PodcastHeader) {
        title.text = header.subTitle
        subtitle.text =  "${header.title} - ${header.videos.size} resultados"
        loading.setIndicatorColor(Color.parseColor(header.highLightColor))
        videosRecycler.adapter = VideosAdapter(header.videos, header.highLightColor)
        loading.setProgress(100, true)
        videosRecycler.fadeIn()
    }

    override fun onDestroy() {
        super.onDestroy()
        playlistFragmentBinding = null
    }
}