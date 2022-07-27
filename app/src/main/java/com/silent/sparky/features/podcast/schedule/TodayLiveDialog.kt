package com.silent.sparky.features.podcast.schedule

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import com.bumptech.glide.Glide
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.alert.BaseAlert
import com.ilustris.ui.alert.DialogStyles
import com.silent.core.videos.Video
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentTodayGuestBinding

class TodayLiveDialog(
    context: Context,
    val placeHolder: String,
    val highLightColor: Int,
    val video: Video,
    var videoClick: (Video) -> Unit,
) : BaseAlert(context, R.layout.fragment_today_guest, DialogStyles.FULL_SCREEN) {


    private fun FragmentTodayGuestBinding.setupView() {
        root.backgroundTintList = ColorStateList.valueOf(highLightColor)
        fragmentSubtitle.text = video.title
        seeGuestButton.setTextColor(highLightColor)
        hostCard.setOnClickListener {
            videoClick(video)
        }
        seeGuestButton.setOnClickListener {
            videoClick(video)
        }
        Glide.with(context).load(video.thumbnailUrl).error(placeHolder).into(videoThumb)
        root.slideInBottom()
    }

    override fun View.configure() {
        FragmentTodayGuestBinding.bind(this).setupView()
    }

}