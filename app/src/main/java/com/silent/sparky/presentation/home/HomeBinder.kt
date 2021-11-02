package com.silent.sparky.presentation.home

import androidx.recyclerview.widget.RecyclerView
import com.silent.core.data.model.podcast.PodcastHeader
import com.silent.core.data.podcast.Podcast
import com.silent.core.data.presenter.PodcastPresenter
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.ilustriscore.core.view.BaseView
import com.silent.sparky.databinding.HomeFragmentBinding
import com.silent.sparky.presentation.podcast.ProgramActivity
import com.silent.sparky.data.viewmodel.PodcastViewModel
import com.silent.sparky.presentation.components.VideoHeaderAdapter
import com.silent.sparky.presentation.podcast.SampleData

class HomeBinder(override val viewBind: HomeFragmentBinding) : BaseView<Podcast>() {

    override val presenter = PodcastPresenter(this)
    private val viewModel = PodcastViewModel()
    override fun initView() {
        showListData(SampleData.programs())
    }

    override fun showListData(list: List<Podcast>) {
        super.showListData(list)
        val podcastHeaders = ArrayList<PodcastHeader>()
        list.forEachIndexed  { index, podcast ->
            viewModel.getChannelHome(podcast.youtubeID) { uploads, playListId ->
                podcastHeaders.add(PodcastHeader(
                    podcast.name,
                    podcast.iconURL,
                    uploads,
                    playListId,
                    RecyclerView.HORIZONTAL,
                    podcast
                ))
            }
            if (index == list.lastIndex) {
                setupAdapter(podcastHeaders)
            }
        }
    }

    private fun setupAdapter(podcastHeaders: ArrayList<PodcastHeader>) {
        viewBind.channelEpsRecycler.adapter = VideoHeaderAdapter(podcastHeaders) { it ->
            podcastHeaders[it].podcast?.let { podcast -> ProgramActivity.getLaunchIntent(podcast, context) }
        }
        delayedFunction {
            viewBind.homeAppbar.setExpanded(false, true)
        }
    }

}