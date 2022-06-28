package com.silent.sparky.features.podcast.di

import com.silent.core.podcast.PodcastService
import com.silent.core.videos.CutService
import com.silent.core.videos.VideoService
import com.silent.sparky.features.podcast.PodcastViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val podcastModule = module {
    factory { PodcastService() }
    factory { VideoService() }
    factory { CutService()  }
    viewModel { PodcastViewModel(androidApplication(), get(), get(), get()) }
}