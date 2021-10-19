package com.silent.sparky.programs

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.silent.core.program.Program
import com.silent.core.program.ProgramPresenter
import com.silent.ilustriscore.core.view.BaseView
import com.silent.sparky.databinding.ActivityMainBinding
import com.silent.sparky.program.ProgramActivity
import com.silent.sparky.program.ProgramViewModel
import com.silent.sparky.program.adapter.ChannelHeaderAdapter
import com.silent.sparky.program.data.ChannelHeader
import com.silent.sparky.program.data.ProgramHeader

class MainActBinder(override val viewBind: ActivityMainBinding, val lifecycle: LifecycleOwner) : BaseView<Program>() {

    override val presenter = ProgramPresenter(this)
    private val channelHeaderAdapter = ChannelHeaderAdapter(ArrayList())
    val programViewModel = ProgramViewModel()

    override fun initView() {
        val data = SampleData.programs()
        viewBind.programsRecycler.run {
            adapter = ProgramsAdapter(data) {
                ProgramActivity.getLaunchIntent(it, context)
            }
        }
        presenter.loadData()
    }

    override fun showListData(list: List<Program>) {
        super.showListData(list)
        viewBind.channelEpsRecycler.adapter = channelHeaderAdapter
        list.forEach { program ->
            programViewModel.getChannelData(program)
            programViewModel.channelState.observe(lifecycle, { observer ->
                when(observer) {
                    is ProgramViewModel.ChannelState.ChannelDataRetrieved -> {
                        programViewModel.getChannelVideos(observer.channelDetails.contentDetails.relatedPlaylists.uploads)
                    }
                    is ProgramViewModel.ChannelState.ChannelUploadsRetrieved -> {
                        channelHeaderAdapter.updateSection(ChannelHeader(program, observer.videos))
                    }
                    else -> {

                    }
                }
            })
        }
    }
}