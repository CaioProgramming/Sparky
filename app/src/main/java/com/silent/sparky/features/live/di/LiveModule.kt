package com.silent.sparky.features.live.di

import com.silent.core.podcast.PodcastService
import com.silent.core.videos.CutService
import com.silent.core.videos.VideoService
import com.silent.sparky.features.live.LiveViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val liveModule = module {
    factory { VideoService() }
    factory { CutService() }
    factory { PodcastService() }
    viewModel { LiveViewModel(get(), get(), get(), androidApplication()) }
}