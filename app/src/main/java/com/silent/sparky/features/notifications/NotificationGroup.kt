package com.silent.sparky.features.notifications

import com.silent.core.notifications.Notification
import com.silent.core.podcast.Podcast

data class NotificationGroup(
    val podcast: Podcast,
    val notifications: List<Notification>
)
