package com.silent.sparky

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.silent.sparky.features.ErrorActivity

class SparkyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
            .trackActivities(true)
            .errorActivity(ErrorActivity::class.java).apply()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}