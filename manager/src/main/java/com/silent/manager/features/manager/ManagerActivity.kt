package com.silent.manager.features.manager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.silent.manager.R
import com.silent.manager.databinding.ActivityManagerBinding

class ManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.run {
            val navController = findNavController(R.id.manager_host)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            /*val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_dashboard
                )
            )*/
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.podcastsManagerFragment, R.id.podcastFragment
                )
            )
            managerToolbar.setupWithNavController(navController, appBarConfiguration)
        }

    }


}