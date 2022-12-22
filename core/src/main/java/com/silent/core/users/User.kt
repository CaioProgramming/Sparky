package com.silent.core.users

import com.silent.core.notifications.Notification
import com.silent.ilustriscore.core.bean.BaseBean
import java.io.Serializable

const val NEW_USER = "NEW_USER"

data class User(
    override var id: String = "",
    var uid: String = "",
    var token: String = "",
    var flowUserName: String = "",
    var email: String = "",
    var name: String = "",
    var profilePic: String = "",
    var admin: Boolean = false,
    var notifications: ArrayList<Notification> = ArrayList(),
    var notificationSettings: NotificationSettings = NotificationSettings()
) : BaseBean(id), Serializable {
    companion object {
        fun newUser() = User(id = NEW_USER)
    }
}

data class NotificationSettings(
    var episodesEnabled: Boolean = true,
    var liveEnabled: Boolean = true,
    var cutsEnabled: Boolean = false,
    var weekEpisodes: Boolean = false,
) : Serializable


