package com.silent.sparky.features.cuts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.codeboy.pager2_transformers.Pager2_TabletTransformer
import com.codeboy.pager2_transformers.Pager2_VerticalFlipTransformer
import com.codeboy.pager2_transformers.Pager2_ZoomOutTransformer
import com.ilustris.animations.fadeOut
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.extensions.ERROR_COLOR
import com.ilustris.ui.extensions.gone
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.podcast.Podcast
import com.silent.core.videos.Video
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentCutsBinding
import com.silent.sparky.features.cuts.data.PodcastCutHeader
import com.silent.sparky.features.cuts.ui.adapter.CutsAdapter
import com.silent.sparky.features.cuts.ui.adapter.PodcastCutPageAdapter
import com.silent.sparky.features.cuts.viewmodel.CutsState
import com.silent.sparky.features.cuts.viewmodel.CutsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CutsFragment : Fragment() {

    private val cutsBinding : FragmentCutsBinding by lazy { FragmentCutsBinding.bind(requireView()) }
    private val cutsViewModel by viewModel<CutsViewModel>()
    lateinit var cutsAdapter : PodcastCutPageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cuts, container, false)
    }

    private fun navigateToPodcast(podcast: Podcast) {
        val bundle = bundleOf("podcast_id" to podcast.id)
        findNavController().navigate(R.id.action_navigation_cuts_to_podcastFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        observeViewModel()
        cutsViewModel.fetchCuts()
    }

    private fun FragmentCutsBinding.setupCuts(headers: ArrayList<PodcastCutHeader>) {
        cutsAdapter  = PodcastCutPageAdapter(headers, ::navigateToPodcast)
        cutsPager.adapter = cutsAdapter
        cutsPager.setPageTransformer(Pager2_ZoomOutTransformer())
        cutsPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                cutsAdapter.enabledPlayer = position
            }
        })
        cutsBinding.cutsAnimation.fadeOut()

    }

    private fun observeViewModel() {
        cutsViewModel.cutsState.observe(viewLifecycleOwner) {
            when (it) {
                CutsState.CutsError -> view?.showSnackBar(
                    "Ocorre um erro inesperado ao obter os cortes",
                    backColor = ContextCompat.getColor(requireContext(), ERROR_COLOR)
                )
                is CutsState.CutsRetrieved -> {
                    cutsBinding.setupCuts(it.cutHeader)
                }
            }
        }
    }
}