package com.silent.sparky.features.profile.di

import com.silent.core.flow.FlowService
import com.silent.core.podcast.PodcastService
import com.silent.core.preferences.PreferencesService
import com.silent.core.stickers.StickersService
import com.silent.core.users.UsersService
import com.silent.sparky.features.profile.settings.SettingsViewModel
import com.silent.sparky.features.profile.viewmodel.PreferencesViewModel
import com.silent.sparky.features.profile.viewmodel.ProfileViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    factory { PodcastService() }
    factory { UsersService() }
    factory { FlowService() }
    factory { StickersService() }
    viewModel { SettingsViewModel(androidApplication(), get(), get(), get()) }
    viewModel { PreferencesViewModel(androidApplication(), get(), get(), get()) }
    viewModel { ProfileViewModel(androidApplication(), get(), get(), get()) }
}