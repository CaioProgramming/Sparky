package com.silent.sparky.features.player.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.silent.sparky.R
import com.silent.sparky.features.player.view.adapter.PlayerViewPagerAdapter
import com.silent.sparky.features.player.view.fragment.PlayerFragment
import kotlinx.android.synthetic.main.activity_player_videos.*

class PlayerVideosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_videos)
    }

    override fun onStart() {
        super.onStart()
        val fragments = listOf(PlayerFragment.newInstance(), PlayerFragment.newInstance())
        val adapter = PlayerViewPagerAdapter(supportFragmentManager, fragments)
        id_vp_player.adapter = adapter
    }
}