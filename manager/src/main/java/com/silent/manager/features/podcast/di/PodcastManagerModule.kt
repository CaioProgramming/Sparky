package com.silent.manager.features.podcast.di

import com.silent.core.podcast.PodcastService
import com.silent.core.videos.CutService
import com.silent.core.videos.VideoMapper
import com.silent.core.videos.VideoService
import com.silent.core.youtube.YoutubeService
import com.silent.manager.features.podcast.PodcastViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val podcastManagerModule = module {


    factory { PodcastService() }
    factory { YoutubeService() }
    factory { VideoMapper() }

    viewModel { PodcastViewModel(androidApplication(), get(), get(), get(), get(), get()) }
}