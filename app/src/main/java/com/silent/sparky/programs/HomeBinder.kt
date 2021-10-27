package com.silent.sparky.programs

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.silent.core.program.Program
import com.silent.core.program.ProgramPresenter
import com.silent.ilustriscore.core.view.BaseView
import com.silent.sparky.databinding.ActivityMainBinding
import com.silent.sparky.databinding.HomeFragmentBinding
import com.silent.sparky.program.ProgramActivity
import com.silent.sparky.program.ProgramViewModel
import com.silent.sparky.program.adapter.ChannelHeaderAdapter
import com.silent.sparky.program.adapter.VideoHeaderAdapter
import com.silent.sparky.program.data.ProgramHeader

class HomeBinder(override val viewBind: HomeFragmentBinding, val lifecycleOwner: LifecycleOwner) : BaseView<Program>() {

    override val presenter = ProgramPresenter(this)
    val viewModel = ProgramViewModel()
    override fun initView() {
        showListData(SampleData.programs())
    }

    override fun showListData(list: List<Program>) {
        super.showListData(list)
        viewBind.programsRecycler.run {
            adapter = ProgramsAdapter(list) {
                ProgramActivity.getLaunchIntent(it, context)
            }
        }
        viewModel.getChannelVideos("UUmw6h7iv_A_nHA1nlnhkAAA")
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.channelState.observe(lifecycleOwner, {
            when(it) {
                is ProgramViewModel.ChannelState.ChannelUploadsRetrieved -> {
                    val programSections = ArrayList<ProgramHeader>()
                    programSections.add(ProgramHeader("Ùltimos envios dos estúdios flow", it.videos, it.playlistId))
                    viewBind.channelEpsRecycler.adapter = VideoHeaderAdapter(programSections)

                }
                else -> {

                }
            }
        })
    }
}