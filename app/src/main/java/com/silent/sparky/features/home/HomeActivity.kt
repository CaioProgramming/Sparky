package com.silent.sparky.features.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.silent.ilustriscore.core.utilities.LoginHelper
import com.silent.sparky.R
import com.silent.sparky.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        val navView: BottomNavigationView = homeBinding.navView

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