package com.silent.sparky.di

import com.silent.core.firebase.FirebaseService
import com.silent.core.podcast.PodcastService
import com.silent.core.preferences.PreferencesService
import com.silent.core.users.UsersService
import com.silent.core.videos.VideoMapper
import com.silent.core.youtube.YoutubeService
import com.silent.sparky.features.home.viewmodel.MainActViewModel
import com.silent.sparky.features.notifications.NotificationViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { PreferencesService(androidApplication()) }
    single { FirebaseService() }
    single { YoutubeService() }
    single { VideoMapper() }
    single { UsersService() }
    factory { PodcastService() }
    viewModel { MainActViewModel(androidApplication(), get(), get(), get()) }
    viewModel { NotificationViewModel(androidApplication(), get(), get()) }
}

