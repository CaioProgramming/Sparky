package com.silent.sparky.presentation.cuts

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.silent.core.data.program.Podcast
import com.silent.core.data.presenter.PodcastPresenter
import com.silent.core.youtube.PlaylistResource
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.ilustriscore.core.view.BaseView
import com.silent.sparky.presentation.components.PagerStack
import com.silent.sparky.databinding.FragmentCutsBinding
import com.silent.sparky.data.viewmodel.PodcastViewModel
import com.silent.sparky.presentation.podcast.SampleData

class CutsBinder(override val viewBind: FragmentCutsBinding, val lifecycleOwner: LifecycleOwner): BaseView<Podcast>() {

    override val presenter = PodcastPresenter(this)
    private val viewModel = PodcastViewModel()
    private val cutsAdapter = CutsAdapter(ArrayList())

    override fun initView() {
        showListData(SampleData.programs())
    }

    override fun showListData(list: List<Podcast>) {
        super.showListData(list)
        list.forEachIndexed { index, program ->
            viewModel.podcastState.observe(lifecycleOwner, {
                when(it) {
                    is PodcastViewModel.PodcastState.PodcastCutsRetrieved -> {
                        if (index == list.lastIndex) {
                            updateCuts(it.videos)
                        }
                    }
                    else -> {

                    }
                }
            })

            if (index > 0) {
                delayedFunction(60000) {
                    viewModel.getChannelCuts(program.cuts)
                }
            } else {
                viewModel.getChannelCuts(program.cuts)
            }
        }
    }

    private fun updateCuts(videos: List<PlaylistResource>) {
        cutsAdapter.cuts.addAll(ArrayList(videos))
        viewBind.cutsPager.adapter = cutsAdapter
        viewBind.cutsPager.offscreenPageLimit = 5
        viewBind.cutsPager.setPageTransformer(true, PagerStack())
        if (viewBind.cutsAnimation.visibility == View.VISIBLE) {
            viewBind.cutsAnimation.fadeOut()
            viewBind.cutsPager.fadeIn()
        }
    }

}