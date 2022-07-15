package com.silent.sparky.features.podcast.schedule

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.codeboy.pager2_transformers.*
import com.ilustris.animations.fadeOut
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.alert.BaseAlert
import com.silent.core.podcast.Host
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentTodayGuestBinding

class PodcastScheduleDialog(
    context: Context,
    var position: Int,
    var guestList: List<Host>,
    var highlightColor: String,
    var selectGuest: (Host) -> Unit,
) : BaseAlert(context, R.layout.fragment_today_guest) {


    private fun FragmentTodayGuestBinding.setupView() {
        guestsPage.adapter = SchedulePagerAdapter(guestList, highlightColor)
        guestsPage.setPageTransformer(Pager2_FadeOutTransformer())
        guestsPage.postDelayed({
            guestsPage.currentItem = position
            guestsPage.slideInBottom()
        }, 2000)
        seeGuestButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor(highlightColor))
        guestsPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val guest = guestList[position]
                if (guest.socialUrl.isNotEmpty()) {
                    seeGuestButton.slideInBottom()
                } else {
                    seeGuestButton.fadeOut()
                }
                seeGuestButton.setOnClickListener {
                    selectGuest(guest)
                    dialog.dismiss()
                }
            }
        })
    }

    override fun View.configure() {
        FragmentTodayGuestBinding.bind(this).setupView()
    }

}