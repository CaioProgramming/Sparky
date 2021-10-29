package com.silent.sparky.presentation.home

import androidx.lifecycle.LifecycleOwner
import com.silent.core.data.program.Podcast
import com.silent.core.data.presenter.PodcastPresenter
import com.silent.core.data.program.ProgramHeader
import com.silent.ilustriscore.core.view.BaseView
import com.silent.sparky.databinding.HomeFragmentBinding
import com.silent.sparky.presentation.podcast.ProgramActivity
import com.silent.sparky.data.viewmodel.PodcastViewModel
import com.silent.sparky.presentation.components.VideoHeaderAdapter
import com.silent.sparky.presentation.podcast.adapter.PodcastAdapter
import com.silent.sparky.presentation.podcast.SampleData

class HomeBinder(override val viewBind: HomeFragmentBinding, val lifecycleOwner: LifecycleOwner) : BaseView<Podcast>() {

    override val presenter = PodcastPresenter(this)
    val viewModel = PodcastViewModel()
    override fun initView() {
        showListData(SampleData.programs())
    }

    override fun showListData(list: List<Podcast>) {
        super.showListData(list)
        viewBind.programsRecycler.run {
            adapter = PodcastAdapter(list, {
                ProgramActivity.getLaunchIntent(it, context)
            }, false)
        }
        viewModel.getChannelVideos("UUmw6h7iv_A_nHA1nlnhkAAA")
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.podcastState.observe(lifecycleOwner, {
            when(it) {
                is PodcastViewModel.PodcastState.PodcastUploadsRetrieved -> {
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