package com.silent.sparky.features.home.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.silent.core.firebase.FirebaseService
import com.silent.core.podcast.Podcast
import com.silent.core.preferences.PreferencesService
import com.silent.core.videos.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActViewModel(
    application: Application,
    private val firebaseService: FirebaseService,
    private val preferencesService: PreferencesService
) : AndroidViewModel(application) {

    sealed class MainActState {
        data class RetrieveToken(val token: String) : MainActState()
        data class NavigateToPodcast(val podcastId: String, val liveVideo: Video?) : MainActState()
        object RequireLoginState : MainActState()
        object LoginSuccessState : MainActState()
        object LoginErrorState : MainActState()
        object NotificationOpenedState: MainActState()

    }

    sealed class NotificationState {
        object RequestNotification : NotificationState()
        object NotificationGranted : NotificationState()
        object NotificationRevoked : NotificationState()
    }

    val actState = MutableLiveData<MainActState>()
    val notificationState = MutableLiveData<NotificationState>()

    fun updateState(mainActState: MainActState) {
        actState.postValue(mainActState)
    }

    fun updateNotificationPermission(permissionStatus: Boolean = false) {
        if (permissionStatus) {
            notificationState.value = NotificationState.NotificationGranted
            viewModelScope.launch {
                firebaseService.subscribeToTopic {}
            }
        } else {
            notificationState.value = NotificationState.NotificationRevoked
        }
    }

    fun checkNotifications() {
        val permissionStatus = if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                getApplication<Application>().applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (!permissionStatus) {
            notificationState.value = NotificationState.RequestNotification
        } else {
            updateNotificationPermission(permissionStatus)
        }
    }

    fun checkToken() {
        viewModelScope.launch(Dispatchers.IO) { firebaseService.generateFirebaseToken() }
    }

    fun validatePush(podcastExtra: Podcast?, videoExtra: Video?) {
        podcastExtra?.let {
            actState.value = MainActState.NavigateToPodcast(podcastExtra.id, videoExtra)
        }
    }

    fun notificationOpen() {
        actState.value = MainActState.LoginSuccessState
    }

    fun checkUser() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            updateState(MainActState.RequireLoginState)
        } else {
            updateState(MainActState.LoginSuccessState)
        }
    }

}