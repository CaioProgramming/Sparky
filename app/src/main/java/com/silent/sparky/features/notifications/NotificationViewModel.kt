package com.silent.sparky.features.notifications

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.notifications.Notification
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.users.User
import com.silent.core.users.UsersService
import com.silent.ilustriscore.core.model.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel(
    application: Application,
    override val service: UsersService,
    val podcastService: PodcastService,
) : BaseViewModel<User>(application) {

    sealed class NotificationState {
        data class NotificationsFetched(
            val user: User,
            val notificationsGroups: List<NotificationGroup>
        ) : NotificationState()

        data class RedirectNotification(val podcastId: String, val videoId: String? = null) :
            NotificationState()

        object EmptyNotifications : NotificationState()
        object NotificationFetchError : NotificationState()
    }

    val notificationState = MutableLiveData<NotificationState>()


    fun fetchNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userRequest = service.getSingleData(service.currentUser()!!.uid)
                if (userRequest.isSuccess) {
                    val user = userRequest.success.data as User
                    val notificationGroup = ArrayList<NotificationGroup>()
                    val groupedNotifications = user.notifications.groupBy { it.podcastId }
                    groupedNotifications.forEach { (id, notifications) ->
                        val podcast = podcastService.getSingleData(id!!)
                        if (podcast.isSuccess) {
                            notificationGroup.add(
                                NotificationGroup(
                                    podcast.success.data as Podcast,
                                    notifications.sortedBy { it.sent_at })
                            )
                        }
                    }
                    if (groupedNotifications.isNotEmpty()) {
                        notificationState.postValue(
                            NotificationState.NotificationsFetched(
                                user,
                                notificationGroup.sortedByDescending { it.podcast.subscribe })
                        )
                    } else {
                        notificationState.postValue(NotificationState.EmptyNotifications)
                    }
                } else {
                    notificationState.postValue(NotificationState.NotificationFetchError)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                notificationState.postValue(NotificationState.NotificationFetchError)
            }
        }
    }

    fun openNotification(user: User, notification: Notification) {
        val notificationIndex = user.notifications.indexOf(notification)
        user.notifications[notificationIndex] = notification.apply { open = true }
        editData(user)
        notificationState.postValue(notification.podcastId?.let {
            NotificationState.RedirectNotification(
                it, notification.videoId
            )
        })
    }

}