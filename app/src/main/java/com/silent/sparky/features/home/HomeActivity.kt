package com.silent.sparky.features.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.silent.ilustriscore.core.utilities.LoginHelper
import com.silent.sparky.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(R.layout.activity_home) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navView: BottomNavigationView = nav_view

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )*/
        navView.setupWithNavController(navController)
        LoginHelper.signIn(
            this,
            arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build()),
            R.style.Theme_Sparky,
            R.mipmap.ic_launcher
        )
    }
}