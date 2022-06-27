package com.silent.sparky.features.cuts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codeboy.pager2_transformers.Pager2_CubeInDepthTransformer
import com.codeboy.pager2_transformers.Pager2_ParallaxTransformer
import com.codeboy.pager2_transformers.Pager2_PopTransformer
import com.ilustris.animations.popOut
import com.ilustris.ui.extensions.ERROR_COLOR
import com.ilustris.ui.extensions.gone
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.videos.Video
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentCutsBinding
import com.silent.sparky.features.cuts.viewmodel.CutsState
import com.silent.sparky.features.cuts.viewmodel.CutsViewModel

class CutsFragment : Fragment() {

    private val cutsBinding : FragmentCutsBinding by lazy { FragmentCutsBinding.bind(requireView()) }
    private val cutsViewModel by lazy { CutsViewModel(requireActivity().application) }
    lateinit var cutsAdapter : CutsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cuts, container, false)
    }

    override fun onStart() {
        super.onStart()
        cutsBinding.cutsPager.run {
            cutsAdapter = CutsAdapter(ArrayList()) {
                val bundle = bundleOf("podcast_id" to it.id)
                findNavController().navigate(R.id.action_navigation_cuts_to_podcastFragment, bundle)
            }
            adapter = cutsAdapter
        }
        observeViewModel()
        cutsViewModel.fetchCuts()
    }

    private fun updateCuts(videos: ArrayList<Video>) {
        cutsAdapter.updateCuts(videos)
        cutsBinding.cutsAnimation.gone()
    }

    private fun observeViewModel() {
        cutsViewModel.cutsState.observe(viewLifecycleOwner) {
            when (it) {
                CutsState.CutsError -> view?.showSnackBar(
                    "Ocorre um erro inesperado ao obter os cortes",
                    backColor = ContextCompat.getColor(requireContext(), ERROR_COLOR)
                )
                is CutsState.CutsRetrieved -> {
                    updateCuts(it.videos)
                }
            }
        }
    }
}