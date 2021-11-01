package com.silent.newpodcasts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.silent.newpodcasts.R
import kotlinx.android.synthetic.main.fragment_podcast_youtubedata.*

class PodcastGetYoutubeDataFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_podcast_youtubedata, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        next_button.setOnClickListener {
            findNavController().navigate(R.id.action_podcastGetYoutubeDataFragment_to_podcastGetHostsFragment)
        }
    }
}