package com.silent.sparky.features.player.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class PlayerViewPagerAdapter(fragmentManager: FragmentManager,private val fragmentList: List<Fragment>) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int = fragmentList.count()

    override fun getItem(position: Int): Fragment = fragmentList[position]
}