package com.silent.navigation

import android.content.Context
import android.content.Intent
import com.silent.newpodcasts.NewPodcastActivity


enum class ModuleNavigator(val className: String) {
    NEW_PODCAST(NewPodcastActivity::class.java.name)
}
class NavigationUtils(val context: Context) {

    fun startModule(moduleNavigator: ModuleNavigator) {
        context.startActivity(Intent(context, Class.forName(moduleNavigator.className)))
    }

}