package com.silent.sparky.features.profile.di

import com.silent.core.flow.FlowService
import com.silent.core.users.UsersService
import com.silent.sparky.features.profile.viewmodel.ProfileViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {

    factory { UsersService() }
    factory { FlowService() }
    viewModel { ProfileViewModel(androidApplication(), get(), get()) }
}