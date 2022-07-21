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

    fun openInstagram(userID: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW,
            Uri.parse(INSTAGRAM_PREFIX + userID))
        context.startActivity(browserIntent)
    }

    fun openTwitch(twitchID: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(TWITCH_PREFIX + twitchID)
        )
        context.startActivity(browserIntent)
    }

    fun openTwitter(twitterID: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(TWITTER_PREFIX + twitterID)
        )
        context.startActivity(browserIntent)
    }

    fun openNv99() {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(NV99_URL)
        )
        context.startActivity(browserIntent)
    }

}