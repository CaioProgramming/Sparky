package com.silent.sparky.features.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.silent.core.program.Podcast
import com.silent.core.utils.WebUtils
import com.silent.ilustriscore.core.utilities.gone
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.sparky.R
import com.silent.sparky.data.PodcastHeader
import com.silent.sparky.features.home.adapter.ProgramsAdapter
import com.silent.sparky.features.home.adapter.VideoHeaderAdapter
import com.silent.sparky.features.home.data.LiveHeader
import com.silent.sparky.features.home.viewmodel.HomeState
import com.silent.sparky.features.home.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.live_bottom_sheet.*

class HomeFragment : Fragment() {

    private val homeViewModel = HomeViewModel()
    private val videoHeaderAdapter = VideoHeaderAdapter(
        ArrayList(),
        ::openPodcast,
        ::openChannel
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    private fun openPodcast(podcastHeader: PodcastHeader) {
        val bundle = bundleOf("podcast_id" to podcastHeader.playlistId)
        findNavController().navigate(R.id.action_navigation_home_to_podcastFragment, bundle)
    }

    private fun openChannel(url: String) {
        WebUtils(requireContext()).openYoutubeChannel(url)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupView()
        homeViewModel.getHome()
    }

    private fun setupView() {
        podcasts_resume_recycler.adapter = videoHeaderAdapter
        videoHeaderAdapter.clearAdapter()
    }

    private fun observeViewModel() {
        homeViewModel.homeState.observe(this, {
            when (it) {
                is HomeState.HomeChannelRetrieved -> {
                    videoHeaderAdapter.updateSection(it.podcastHeader)

                }
                HomeState.HomeError -> {
                    view?.showSnackBar(
                        "Ocorreu um erro inesperado ao obter videos dos programas",
                        backColor = Color.RED
                    )
                }
                HomeState.HomeLiveError -> {
                    view?.showSnackBar(
                        "Ocorreu um erro inesperado ao verificar as lives",
                        backColor = Color.RED
                    )
                }
                is HomeState.HomeLivesRetrieved -> {
                    if (it.podcasts.isEmpty()) {
                        lives_sheet.gone()
                        live_container.fadeOut()
                    } else {
                        live_container.fadeIn()
                        lives_recycler_view.adapter =
                            ProgramsAdapter(extractPodcasts(it.podcasts)) { podcast, index ->
                                val bundle = bundleOf("live_object" to it.podcasts[index])
                                findNavController().navigate(
                                    R.id.action_navigation_home_to_liveFragment,
                                    bundle
                                )
                            }
                    }
                    view?.showSnackBar("${it.podcasts.size} lives no momento")
                }
            }
        })
    }

    private fun extractPodcasts(liveHeader: ArrayList<LiveHeader>): ArrayList<Podcast> {
        val podcasts = ArrayList<Podcast>()
        liveHeader.forEach {
            podcasts.add(it.podcast)
        }
        return podcasts
    }

}