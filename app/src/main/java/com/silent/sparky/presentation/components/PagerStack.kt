package com.silent.sparky.presentation.components

import android.view.View
import androidx.viewpager.widget.ViewPager

class PagerStack: ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        if (position >= 0) {
            page.scaleX = 0.9f - 0.05f * position
            page.scaleY = 0.9f
            page.translationX = -page.width * position
            page.translationY = 20 * position
            page.alpha = 1f - 0.3f * position
        } else {
            page.alpha = 1 + 0.3f * position
            page.scaleX = 0.9f + 0.05f * position
            page.scaleY = 0.9f
            page.translationY = page.width * position
            page.translationX = 30 * position
        }
    }

}