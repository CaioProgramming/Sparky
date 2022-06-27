package com.silent.manager.features.manager.update

import android.content.Context
import android.view.View
import com.ilustris.ui.alert.BaseAlert
import com.ilustris.ui.alert.DialogStyles
import com.silent.core.podcast.podcasts
import com.silent.manager.R
import com.silent.manager.databinding.PodcastUpdateDialogBinding

class PodcastUpdateDialog(context: Context, podcasts: podcasts) : BaseAlert(context, R.layout.podcast_update_dialog, style = DialogStyles.BOTTOM_NO_BORDER) {
    val adapter = PodcastUpdateAdapter(podcasts)
    override fun View.configure() {
        PodcastUpdateDialogBinding.bind(this).run {
            podcastsRecycler.adapter = adapter
        }
    }

    fun updatePodcastStatus(position: Int) {
        adapter.updatePodcast(position, false)
    }

}