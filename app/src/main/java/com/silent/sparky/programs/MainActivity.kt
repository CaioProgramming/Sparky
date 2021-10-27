package com.silent.sparky.programs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.silent.sparky.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        home_pager.adapter = MainActAdapter(this)
        TabLayoutMediator(main_tabs, home_pager) { tab, position ->
            when(position) {
                0 -> tab.text = "Home"
                1 -> tab.text = "Cortes"
            }

        }.attach()
    }
}