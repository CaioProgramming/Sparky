package com.silent.newpodcasts.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.silent.newpodcasts.R
import com.silent.newpodcasts.components.PodcastAdapter
import com.silent.newpodcasts.data.viewmodel.NewPodcastViewModel
import kotlinx.android.synthetic.main.fragment_podcast_youtubedata.*

private const val CHANNEL_ID = "UCTBhsXf_XRxk8w4rMj6WBOA"

class PodcastGetYoutubeDataFragment : Fragment() {

    private val podcastAdapter = PodcastAdapter(ArrayList())
    private val viewModel: NewPodcastViewModel by lazy {
        ViewModelProvider(requireActivity()).get(NewPodcastViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_podcast_youtubedata, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flow_related_channels.adapter = podcastAdapter
        next_button.setOnClickListener {
            findNavController().navigate(R.id.action_podcastGetYoutubeDataFragment_to_podcastGetHostsFragment)
        }
        viewModel.getRelatedChannels(CHANNEL_ID)
        viewModel.newPodcastState.observe(viewLifecycleOwner, {
            when (it) {
                NewPodcastViewModel.NewPodcastState.NewPodcastError -> TODO()
                is NewPodcastViewModel.NewPodcastState.UploadNewPodcastState -> TODO()
                is NewPodcastViewModel.NewPodcastState.NewPodCastChannelsRetrieved -> {
                    flow_related_channels.adapter = PodcastAdapter(it.channels)

                }
                is NewPodcastViewModel.NewPodcastState.NewPodCastRetrievedState -> TODO()
            }
        })
    }
}