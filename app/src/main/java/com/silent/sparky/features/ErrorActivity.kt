package com.silent.sparky.features

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.silent.core.R
import com.silent.sparky.features.home.HomeActivity

class ErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        applicationContext.startActivity(
            Intent(
                applicationContext,
                HomeActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
    }
}