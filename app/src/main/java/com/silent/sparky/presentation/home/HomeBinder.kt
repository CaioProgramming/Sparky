package com.silent.sparky.presentation.home

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilustris.animations.*
import com.silent.core.data.model.podcast.PodcastHeader
import com.silent.core.data.podcast.Podcast
import com.silent.core.data.presenter.PodcastPresenter
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.ilustriscore.core.utilities.visible
import com.silent.ilustriscore.core.view.BaseView
import com.silent.sparky.databinding.HomeFragmentBinding
import com.silent.sparky.presentation.podcast.ProgramActivity
import com.silent.sparky.data.viewmodel.PodcastViewModel
import com.silent.sparky.presentation.components.VideoHeaderAdapter
import com.silent.sparky.presentation.podcast.SampleData

class HomeBinder(override val viewBind: HomeFragmentBinding) : BaseView<Podcast>() {

    override val presenter = PodcastPresenter(this)
    private val viewModel = PodcastViewModel()
    private var podcastHeadersAdapter: VideoHeaderAdapter? = null
    override fun initView() {
        showListData(SampleData.programs())
    }

    override fun showListData(list: List<Podcast>) {
        super.showListData(list)

        list.forEachIndexed  { index, podcast ->
            viewModel.getChannelHome(podcast.youtubeID) { uploads, playListId ->
                if (podcastHeadersAdapter == null) {
                    val podcastHeaders = ArrayList<PodcastHeader>().apply {
                        add(PodcastHeader(
                            podcast.name,
                            podcast.iconURL,
                            uploads,
                            playListId,
                            RecyclerView.HORIZONTAL
                        ))
                    }
                    viewBind.channelEpsRecycler.run {
                        adapter = VideoHeaderAdapter(programSections = podcastHeaders) {
                            ProgramActivity.getLaunchIntent(list[it], context)
                        }
                        podcastHeadersAdapter = adapter as VideoHeaderAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    }

                } else {
                    podcastHeadersAdapter?.updateSection(
                        PodcastHeader(
                            podcast.name,
                            podcast.iconURL,
                            uploads,
                            playListId,
                            RecyclerView.HORIZONTAL
                        )
                    )
                }
            }
            if (index == list.lastIndex) {
                delayedFunction(5000) {
                    viewBind.homeAppbar.setExpanded(false, true)
                }
            }
        }

    }

}