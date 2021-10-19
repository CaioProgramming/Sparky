package com.silent.sparky.programs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.silent.ilustriscore.core.utilities.getView
import com.silent.sparky.R
import com.silent.sparky.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActBinder(DataBindingUtil.setContentView(this, R.layout.activity_main),this).initView()
    }
}