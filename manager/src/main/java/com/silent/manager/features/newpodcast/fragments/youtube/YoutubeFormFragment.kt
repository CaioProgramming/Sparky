package com.silent.manager.features.newpodcast.fragments.youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ilustris.ui.extensions.ERROR_COLOR
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.podcast.Podcast
import com.silent.manager.R
import com.silent.manager.databinding.FragmentPodcastYoutubedataBinding
import com.silent.manager.features.newpodcast.NewPodcastViewModel
import com.silent.manager.states.NewPodcastState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class YoutubeFormFragment : Fragment() {

    private val relatedChannelsAdapter =
        PodcastHeaderAdapter(ArrayList(), onSelectPodcast = ::selectPodcast)
    private val newPodcastViewModel: NewPodcastViewModel by sharedViewModel()

    private fun selectPodcast(podcast: Podcast) {
        newPodcastViewModel.checkPodcast(podcast)
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
        FragmentPodcastYoutubedataBinding.bind(view).setupView()
        relatedChannelsAdapter.clearAdapter()
        newPodcastViewModel.getRelatedChannels("ESTÚDIOS FLOW")
        observeViewModel()
    }

    private fun FragmentPodcastYoutubedataBinding.setupView() {
        flowRelatedChannels.adapter = relatedChannelsAdapter
    }

    private fun observeViewModel() {
        newPodcastViewModel.newPodcastState.observe(viewLifecycleOwner) {
            when (it) {
                is NewPodcastState.RelatedPodcastsRetrieved -> {
                    if (it.podcastsHeader.isNotEmpty()) {
                        relatedChannelsAdapter.updateHeaders(ArrayList(it.podcastsHeader))
                    } else {
                        view?.showSnackBar(
                            "Todos os podcasts foram cadastrados.",
                            backColor = ContextCompat.getColor(
                                requireContext(),
                                ERROR_COLOR
                            )
                        )
                    }
                }

                NewPodcastState.InvalidPodcast -> {
                    view?.showSnackBar(
                        "Esse podcast já foi cadastrado, selecione outro.",
                        backColor = ContextCompat.getColor(
                            requireContext(),
                           ERROR_COLOR
                        )
                    )
                }

                is NewPodcastState.ValidPodcast -> {
                    confirmPodcast(it.podcast)
                }

                is NewPodcastState.PodcastUpdated -> {
                    findNavController().navigate(R.id.action_podcastGetYoutubeFormFragment_to_GetHostsFragment)
                }

                else -> {

                }
            }
        }
    }

    private fun confirmPodcast(podcast: Podcast) {
        PodcastDialog.newInstance(podcast) {
            selectCutChannel(podcast)
        }.show(requireActivity().supportFragmentManager, "SELECT_PODCAST")
    }

    private fun selectCutChannel(savedPodcast: Podcast) {
        CutsDialog.getInstance { cutPodcast ->
            savedPodcast.cuts = cutPodcast.uploads
            newPodcastViewModel.updatePodcast(savedPodcast)
        }.show(childFragmentManager, "SELECT_CUT")
    }
}