package com.silent.sparky.features.player.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener
import com.silent.sparky.R
import kotlinx.android.synthetic.main.fragment_player_video.*


class PlayerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_player_video, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id_youtube_player_view.initialize(
            { initializedYouTubePlayer ->
                initializedYouTubePlayer.addListener(
                    object : AbstractYouTubePlayerListener() {
                        override fun onReady() {
                            initializedYouTubePlayer.loadVideo("MH-9SUubQa0", 0F)
                            initializedYouTubePlayer.play()
                        }
                    })
            }, true
        )
    }

    companion object {
        fun newInstance() = PlayerFragment()
    }

}