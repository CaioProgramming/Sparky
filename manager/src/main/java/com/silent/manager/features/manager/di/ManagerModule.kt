package com.silent.manager.features.manager.di

import com.silent.core.podcast.PodcastService
import com.silent.core.videos.CutService
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.manager.features.manager.ManagerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val managerModule = module {
    factory { VideoMapper() }
    factory { CutService() }
    factory { VideoService() }
    factory { YoutubeService() }
    factory { PodcastService() }
    viewModel { ManagerViewModel(androidApplication(), get(), get(), get(), get(), get()) }
}