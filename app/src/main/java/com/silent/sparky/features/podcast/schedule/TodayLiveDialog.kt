package com.silent.sparky.features.podcast.schedule

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.bumptech.glide.Glide
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.alert.BaseAlert
import com.ilustris.ui.alert.DialogStyles
import com.silent.core.utils.ImageUtils
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
        fragmentSubtitle.text = video.title
        video.podcast?.let {
            fragmentCaption.text = it.name
            Glide.with(context).load(it.iconURL).into(podcastIcon)
        }
        seeGuestButton.setTextColor(highLightColor)
        seeGuestButton.setOnClickListener {
            dialog.dismiss()
            videoClick(video)
        }
        liveCard.setStrokeColor(ColorStateList.valueOf(highLightColor))
        Glide.with(context)
            .load(ImageUtils.getYoutubeThumb(video.youtubeID, ImageUtils.Quality.MAX))
            .error(placeHolder).into(videoThumb)
        root.slideInBottom()
    }

    override fun View.configure() {
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        FragmentTodayGuestBinding.bind(this).setupView()
    }

}