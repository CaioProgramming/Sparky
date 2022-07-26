package com.silent.sparky.features.cuts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilustris.animations.fadeOut
import com.ilustris.animations.slideInBottom
import com.silent.core.component.showError
import com.silent.core.podcast.Podcast
import com.silent.core.videos.Video
import com.silent.core.videos.VideoType
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentCutsBinding
import com.silent.sparky.features.cuts.ui.adapter.CutGridAdapter
import com.silent.sparky.features.cuts.ui.adapter.PodcastCutPageAdapter
import com.silent.sparky.features.cuts.viewmodel.CutsState
import com.silent.sparky.features.cuts.viewmodel.CutsViewModel
import com.silent.sparky.features.home.data.LiveHeader
import org.koin.androidx.viewmodel.ext.android.viewModel

class CutsFragment : Fragment() {

    private var cutsBinding: FragmentCutsBinding? = null
    private val cutsViewModel by viewModel<CutsViewModel>()
    lateinit var cutsAdapter: PodcastCutPageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cutsBinding = FragmentCutsBinding.inflate(inflater)
        return cutsBinding?.root
    }

    private fun navigateToPodcast(podcast: Podcast) {
        val bundle = bundleOf("podcast_id" to podcast.id)
        findNavController().navigate(R.id.action_navigation_cuts_to_podcastFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        cutsBinding?.errorView?.errorAnimation?.setAnimationFromUrl("https://assets5.lottiefiles.com/packages/lf20_753sjt3m.json")
        observeViewModel()
        cutsViewModel.fetchCuts()
    }


    private fun selectVideo(video: Video) {
        video.podcast?.let {
            val liveObject = LiveHeader(it, video.title, video.youtubeID)
            val bundle = bundleOf("live_object" to liveObject)
            findNavController().navigate(R.id.action_navigation_cuts_to_liveFragment, bundle)
        }
    }

    private fun FragmentCutsBinding.setupCuts(videos: ArrayList<Video>) {
        cutsSearch.setQuery("", false)
        val gridLayoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val video = videos[position]
                return when (video.videoType) {
                    VideoType.DEFAULT -> 1
                    VideoType.MEDIUM -> 2
                    VideoType.BIG -> 3
                }
            }
        }
        cutsRecycler.layoutManager = gridLayoutManager
        cutsRecycler.adapter = CutGridAdapter(videos, ::selectVideo)
        cutsRecycler.slideInBottom()
        cutsAnimation.fadeOut()
        cutsSubtitle.text = "${videos.size} resultados"
        cutsSearch.setQuery("", false)
        cutsSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val filteredVideos = videos.filter {
                        it.title.contains(query, true) || it.description.contains(
                            query,
                            true
                        ) || it.podcast?.name?.contains(query, true) == true
                    }
                    cutsRecycler.adapter = CutGridAdapter(ArrayList(filteredVideos), ::selectVideo)
                    cutsSubtitle.text = "${filteredVideos.size} resultados"
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        cutsSearch.setOnCloseListener {
            cutsRecycler.adapter = CutGridAdapter(videos, ::selectVideo)
            cutsSubtitle.text = "${videos.size} resultados"
            return@setOnCloseListener false
        }
        cutsSearch.setOnSearchClickListener {
            val query = cutsSearch.query
            val filteredVideos = videos.filter {
                it.title.contains(query) || it.description.contains(query) || it.podcast?.name?.contains(
                    query
                ) == true
            }
            cutsRecycler.adapter = CutGridAdapter(ArrayList(filteredVideos), ::selectVideo)
            cutsSubtitle.text = "${filteredVideos.size} resultados"
        }
        val closeButton: View? = cutsSearch.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButton?.setOnClickListener {
            cutsRecycler.adapter = CutGridAdapter(videos, ::selectVideo)
            cutsSubtitle.text = "${videos.size} resultados"
        }
    }

    private fun observeViewModel() {
        cutsViewModel.cutsState.observe(viewLifecycleOwner) {
            when (it) {
                CutsState.CutsError -> {
                    cutsBinding?.errorView?.showError("Ocorre um erro inesperado ao obter os cortes") {
                        cutsViewModel.fetchCuts()
                    }
                }
                is CutsState.CutsRetrieved -> {
                    cutsBinding?.setupCuts(it.videos)
                }
            }
        }
    }
}