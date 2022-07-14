package com.silent.sparky.di

import com.silent.core.firebase.FirebaseService
import com.silent.core.preferences.PreferencesService
import com.silent.core.videos.VideoMapper
import com.silent.core.youtube.YoutubeService
import com.silent.sparky.features.home.viewmodel.MainActViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { PreferencesService(androidApplication()) }
    factory { FirebaseService() }
    single { YoutubeService() }
    single { VideoMapper() }
    viewModel { MainActViewModel(androidApplication(), get(), get()) }
}

