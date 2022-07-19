package com.silent.manager.features.newpodcast.di

import com.silent.core.podcast.PodcastMapper
import com.silent.core.podcast.PodcastService
import com.silent.core.videos.CutService
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.manager.features.newpodcast.NewPodcastViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newPodcastModule = module {

    factory { PodcastService() }
    factory { YoutubeService() }
    factory { PodcastMapper() }
    factory { VideoMapper() }
    factory { VideoService() }
    factory { CutService() }
    viewModel { NewPodcastViewModel(androidApplication(), get(), get(), get(), get(), get(), get()) }
}