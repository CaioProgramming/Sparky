package com.silent.sparky.features.cuts.ui

import android.animation.ValueAnimator
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
import com.silent.core.podcast.podcasts
import com.silent.core.videos.Video
import com.silent.core.videos.VideoType
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentCutsBinding
import com.silent.sparky.features.cuts.ui.adapter.CutGridAdapter
import com.silent.sparky.features.cuts.ui.adapter.PodcastsCutsAdapter
import com.silent.sparky.features.cuts.viewmodel.CutsState
import com.silent.sparky.features.cuts.viewmodel.CutsViewModel
import com.silent.sparky.features.live.data.LiveHeader
import com.silent.sparky.features.live.data.VideoMedia
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.NumberFormat

class CutsFragment : Fragment() {

    lateinit var podcastsCutsAdapter: PodcastsCutsAdapter
    private var cutsBinding: FragmentCutsBinding? = null
    private val cutsViewModel by viewModel<CutsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cutsBinding = FragmentCutsBinding.inflate(inflater)
        return cutsBinding?.root
    }

    override fun onStart() {
        super.onStart()
        cutsBinding?.errorView?.errorAnimation?.setAnimationFromUrl("https://assets5.lottiefiles.com/packages/lf20_753sjt3m.json")
        observeViewModel()
        cutsViewModel.fetchCuts()
    }


    private fun selectVideo(video: Video) {
        video.podcast?.let {
            val liveObject =
                LiveHeader(it, video, VideoMedia.CUT)
            val bundle = bundleOf("live_object" to liveObject)
            findNavController().navigate(R.id.action_navigation_cuts_to_liveFragment, bundle)
        }
    }

    private fun FragmentCutsBinding.setupCuts(videos: ArrayList<Video>, podcasts: podcasts) {
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
        updateSubtitle(videos.size)
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
                    updateSubtitle(filteredVideos.size)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        cutsSearch.setOnCloseListener {
            cutsRecycler.adapter = CutGridAdapter(videos, ::selectVideo)
            updateSubtitle(videos.size)
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
            updateSubtitle(filteredVideos.size)
        }
        val closeButton: View? = cutsSearch.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButton?.setOnClickListener {
            cutsSearch.setQuery("", false)
            cutsRecycler.adapter = CutGridAdapter(videos, ::selectVideo)
            updateSubtitle(videos.size)
        }
        podcastsCutsAdapter = PodcastsCutsAdapter(podcasts) { podcast ->
            podcastsCutsAdapter.selectPodcast(podcast)
            val filteredVideos = videos.filter {
                podcastsCutsAdapter.selectedPodcasts.contains(it.podcastId)
            }
            if (filteredVideos.isNotEmpty()) {
                cutsRecycler.adapter = CutGridAdapter(ArrayList(filteredVideos), ::selectVideo)
                updateSubtitle(filteredVideos.size)
            } else {
                cutsRecycler.adapter = CutGridAdapter(videos, ::selectVideo)
                updateSubtitle(videos.size)

            }
        }
        cutsBinding?.cutsPodcasts?.adapter = podcastsCutsAdapter
    }

    private fun FragmentCutsBinding.updateSubtitle(newResult: Int) {
        val animator = ValueAnimator()
        animator.run {
            setObjectValues(0, newResult)
            addUpdateListener {
                cutsSubtitle.text = NumberFormat.getInstance()
                    .format(it.animatedValue.toString().toInt()) + " cortes disponíveis"
            }
            duration = 2000
            start()
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
                    cutsBinding?.setupCuts(it.videos, it.podcasts)
                }
            }
        }
    }
}