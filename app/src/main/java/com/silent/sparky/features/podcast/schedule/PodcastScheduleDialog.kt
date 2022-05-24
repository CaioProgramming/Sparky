package com.silent.sparky.features.podcast.schedule

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.codeboy.pager2_transformers.Pager2_DepthTransformer
import com.codeboy.pager2_transformers.Pager2_PopTransformer
import com.codeboy.pager2_transformers.Pager2_StackTransformer
import com.codeboy.pager2_transformers.Pager2_ZoomInTransformer
import com.ilustris.animations.fadeOut
import com.ilustris.animations.repeatBounce
import com.ilustris.animations.slideDown
import com.ilustris.animations.slideInBottom
import com.silent.core.podcast.Host
import com.silent.ilustriscore.core.utilities.DialogStyles
import com.silent.ilustriscore.core.view.BaseAlert
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
        guestsPage.setPageTransformer(Pager2_DepthTransformer())
        guestsPage.postDelayed({
            guestsPage.currentItem = position
            guestsPage.slideInBottom()
        }, 500)
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