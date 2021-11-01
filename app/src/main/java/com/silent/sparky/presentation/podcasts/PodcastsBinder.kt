package com.silent.sparky.presentation.podcasts

import com.silent.core.data.presenter.PodcastPresenter
import com.silent.core.data.podcast.Podcast
import com.silent.ilustriscore.core.view.BaseView
import com.silent.navigation.ModuleNavigator
import com.silent.navigation.NavigationUtils
import com.silent.sparky.databinding.PodcastsFragmentBinding
import com.silent.sparky.presentation.podcast.ProgramActivity
import com.silent.sparky.presentation.podcast.SampleData
import com.silent.sparky.presentation.podcast.adapter.PodcastAdapter

class PodcastsBinder(override val viewBind: PodcastsFragmentBinding) : BaseView<Podcast>() {

    override val presenter = PodcastPresenter(this)

    override fun initView() {
        showListData(SampleData.programs())
        //presenter.loadData()
    }

    override fun showListData(list: List<Podcast>) {
        super.showListData(list)
        viewBind.podcastsRecycler.run {
            adapter = PodcastAdapter(list, { podcast ->
                ProgramActivity.getLaunchIntent(podcast, context)
            }, true)
        }
        viewBind.newPodcastButton.setOnClickListener {
            NavigationUtils(context).startModule(ModuleNavigator.NEW_PODCAST)
        }
    }
}