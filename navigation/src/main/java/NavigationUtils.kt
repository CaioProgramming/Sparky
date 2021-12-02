package com.silent.navigation

import android.content.Context
import android.content.Intent
import com.silent.manager.features.manager.ManagerActivity


enum class ModuleNavigator(val className: String) {
    MANAGER(ManagerActivity::class.java.name)
}

class NavigationUtils(val context: Context) {

    fun startModule(moduleNavigator: ModuleNavigator) {
        context.startActivity(Intent(context, Class.forName(moduleNavigator.className)))
    }

}