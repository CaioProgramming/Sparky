package com.silent.core.data.presenter

import com.silent.core.data.podcast.Podcast
import com.silent.core.data.viewmodel.PodcastModel
import com.silent.ilustriscore.core.presenter.BasePresenter
import com.silent.ilustriscore.core.view.BaseView

class PodcastPresenter(override val view: BaseView<Podcast>): BasePresenter<Podcast>() {
    override val model = PodcastModel(this)
}