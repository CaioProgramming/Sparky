package com.silent.sparky.features.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.sparky.R
import com.silent.sparky.features.home.adapter.VideoHeaderAdapter
import com.silent.sparky.features.home.viewmodel.HomeState
import com.silent.sparky.features.home.viewmodel.HomeViewModel
import com.silent.sparky.features.podcast.data.PodcastHeader
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment: Fragment() {

    private val homeViewModel = HomeViewModel()
    private val videoHeaderAdapter = VideoHeaderAdapter(ArrayList(), ::openPodcast)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        homeViewModel.getHome()
        setupView()
    }

    private fun setupView() {
        podcasts_resume_recycler.adapter = videoHeaderAdapter
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
            }
        })
    }

}