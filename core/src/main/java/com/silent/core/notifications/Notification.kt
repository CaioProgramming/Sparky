package com.silent.core.notifications

import com.silent.core.R
import java.util.*

data class Notification(
    val title: String? = "",
    val message: String? = "",
    val podcastId: String? = "",
    val videoId: String? = "",
    val videoThumbnail: String? = "",
    var open: Boolean = false,
    val type: String = NotificationType.EPISODE.name,
    val sent_at: Date = Date()
) {
    fun getNotificationType() = NotificationType.valueOf(type)
}

enum class NotificationType(val icon: Int) {
    EPISODE(R.drawable.ic_iconmonstr_microphone_13),
    CUT(R.drawable.ic_iconmonstr_cut_3),
    WEEK(R.drawable.ic_sparky_notify)
}
