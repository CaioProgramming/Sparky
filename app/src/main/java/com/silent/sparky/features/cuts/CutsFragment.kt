package com.silent.sparky.features.cuts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codeboy.pager2_transformers.Pager2_PopTransformer
import com.ilustris.animations.fadeOut
import com.ilustris.animations.popOut
import com.silent.core.youtube.PlaylistResource
import com.silent.ilustriscore.core.utilities.gone
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.ilustriscore.core.utilities.visible
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
            cutsAdapter = CutsAdapter(ArrayList())
            adapter = cutsAdapter
            setPageTransformer(Pager2_PopTransformer())
        }
        observeViewModel()
        cutsViewModel.fetchCuts()
    }

    private fun updateCuts(videos: ArrayList<PlaylistResource>) {
        cutsAdapter.updateCuts(videos)
        cutsBinding.cutsAnimation.popOut()
    }

    private fun observeViewModel() {
        cutsViewModel.cutsState.observe(viewLifecycleOwner) {
            when (it) {
                CutsState.CutsError -> view?.showSnackBar(
                    "Ocorre um erro inesperado ao obter os cortes",
                    backColor = Color.RED
                )
                is CutsState.CutsRetrieved -> {
                    updateCuts(it.videos)
                }
            }
        }
    }
}