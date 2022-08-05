package com.silent.sparky.features.home.di

import com.silent.core.podcast.PodcastService
import com.silent.core.users.UsersService
import com.silent.core.videos.VideoService
import com.silent.sparky.features.home.viewmodel.HomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    factory { PodcastService() }
    factory { VideoService() }
    factory { UsersService() }
    viewModel { HomeViewModel(androidApplication(), get(), get(), get(), get()) }
}