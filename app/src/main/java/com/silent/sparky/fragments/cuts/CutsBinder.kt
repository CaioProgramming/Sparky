package com.silent.sparky.fragments.cuts

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.silent.core.program.Program
import com.silent.core.program.ProgramPresenter
import com.silent.core.youtube.PlaylistResource
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.ilustriscore.core.view.BaseView
import com.silent.sparky.databinding.FragmentCutsBinding
import com.silent.sparky.program.ProgramViewModel
import com.silent.sparky.programs.SampleData

class CutsBinder(override val viewBind: FragmentCutsBinding, val lifecycleOwner: LifecycleOwner): BaseView<Program>() {

    override val presenter = ProgramPresenter(this)
    private val viewModel = ProgramViewModel()
    private val cutsAdapter = CutsAdapter(ArrayList())

    override fun initView() {
        showListData(SampleData.programs())
    }

    override fun showListData(list: List<Program>) {
        super.showListData(list)
        list.forEachIndexed { index, program ->
            viewModel.channelState.observe(lifecycleOwner, {
                when(it) {
                    is ProgramViewModel.ChannelState.ChannelCutsRetrieved -> {
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