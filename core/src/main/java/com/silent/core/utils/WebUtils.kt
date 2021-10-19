package com.silent.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

class WebUtils(val context: Context) {

    fun openYoutubeVideo(videoID: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_WATCH_PREFIX + videoID))
        context.startActivity(browserIntent)
    }

    fun openYoutubeChannel(videoID: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_CHANNEL_PREFIX + videoID))
        context.startActivity(browserIntent)
    }

    fun openYoutubePlaylist(playlistId: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_PLAYLIST_PREFIX + playlistId))
        context.startActivity(browserIntent)
    }


}