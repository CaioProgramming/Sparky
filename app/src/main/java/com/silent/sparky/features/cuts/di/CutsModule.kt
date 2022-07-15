package com.silent.sparky.features.cuts.di

import com.silent.core.podcast.PodcastService
import com.silent.core.utils.CUTS_PATH
import com.silent.core.videos.CutService
import com.silent.core.videos.VideoService
import com.silent.sparky.features.cuts.viewmodel.CutsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cutsModule = module {

    factory { CutService() }
    factory { PodcastService() }
    viewModel { CutsViewModel(androidApplication(), get(), get()) }

}