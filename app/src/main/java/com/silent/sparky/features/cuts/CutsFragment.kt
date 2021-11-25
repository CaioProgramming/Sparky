package com.silent.sparky.features.cuts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ilustris.animations.fadeOut
import com.silent.core.youtube.PlaylistResource
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.sparky.R
import com.silent.sparky.features.cuts.viewmodel.CutsState
import com.silent.sparky.features.cuts.viewmodel.CutsViewModel
import kotlinx.android.synthetic.main.fragment_cuts.*

class CutsFragment : Fragment() {

    private val cutsViewModel = CutsViewModel()
    private val cutsAdapter = CutsAdapter(ArrayList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cuts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
        cutsViewModel.fetchCuts()
    }

    private fun setupView() {
        cuts_pager.adapter = cutsAdapter
        cuts_pager.offscreenPageLimit = 5
        cuts_pager.setPageTransformer(true, PagerStack())
    }

    private fun updateCuts(videos: ArrayList<PlaylistResource>) {
        val cutsList = cutsAdapter.cuts
        cutsList.addAll(videos)
        cuts_pager.adapter = cutsAdapter
        cuts_pager.offscreenPageLimit = 5
        cuts_pager.setPageTransformer(true, PagerStack())
        cuts_animation.fadeOut()
    }

    private fun observeViewModel() {
        cutsViewModel.cutsState.observe(this, {
            when (it) {
                CutsState.CutsError -> view?.showSnackBar(
                    "Ocorre um erro inesperado ao obter os cortes",
                    backColor = Color.RED
                )
                is CutsState.CutsRetrieved -> {
                    updateCuts(it.videos)
                }
            }
        })
    }
}