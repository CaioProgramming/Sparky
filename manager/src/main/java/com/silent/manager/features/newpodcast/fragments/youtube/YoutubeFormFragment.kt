package com.silent.manager.features.newpodcast.fragments.youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.silent.core.podcast.Podcast
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.manager.R
import com.silent.manager.features.newpodcast.NewPodcastViewModel
import com.silent.manager.states.NewPodcastState
import kotlinx.android.synthetic.main.fragment_podcast_youtubedata.*

class YoutubeFormFragment : Fragment() {

    private lateinit var contentView: View
    private val relatedChannelsAdapter = PodcastAdapter(ArrayList(), ::selectPodcast)
    private val newPodcastViewModel: NewPodcastViewModel by lazy {
        ViewModelProvider(requireActivity())[NewPodcastViewModel::class.java]
    }

    private fun selectPodcast(podcast: Podcast) {
        newPodcastViewModel.checkPodcast(podcast)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::contentView.isInitialized) {
            contentView = inflater.inflate(R.layout.fragment_podcast_youtubedata, container, false)
        }
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (relatedChannelsAdapter.itemCount == 0) {
            newPodcastViewModel.getRelatedChannels()
            observeViewModel()
            setupView()
        }
    }

    private fun setupView() {
        flow_related_channels.adapter = relatedChannelsAdapter
    }

    private fun observeViewModel() {
        newPodcastViewModel.newPodcastState.observe(this, {
            when (it) {
                is NewPodcastState.RelatedChannelRetrieved -> {
                    relatedChannelsAdapter.updateAdapter(it.podcast)
                }
                is NewPodcastState.ChannelSearchRetrieved -> {
                }
                NewPodcastState.InvalidPodcast -> {
                    view?.showSnackBar(
                        "Esse podcast já foi cadastrado, selecione outro",
                        backColor = ContextCompat.getColor(
                            requireContext(),
                            R.color.material_red500
                        )
                    )
                }

                is NewPodcastState.ValidPodcast -> {
                    PodcastDialog.newInstance(it.podcast) {
                        newPodcastViewModel.updatePodcast(it.podcast)
                    }.show(requireActivity().supportFragmentManager, "SELECT_PODCAST")
                }

                is NewPodcastState.PodcastUpdated -> {
                    findNavController().navigate(R.id.action_podcastGetYoutubeFormFragment_to_GetHostsFragment)
                }
            }
        })
    }
}