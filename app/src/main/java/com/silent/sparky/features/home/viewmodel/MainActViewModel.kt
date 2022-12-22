package com.silent.sparky.features.home.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.silent.core.firebase.FirebaseService
import com.silent.core.preferences.PreferencesService
import com.silent.core.users.User
import com.silent.core.users.UsersService
import com.silent.core.utils.TOKEN_PREFERENCES
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActViewModel(
    application: Application,
    private val firebaseService: FirebaseService,
    private val preferencesService: PreferencesService,
    private val usersService: UsersService
) : AndroidViewModel(application) {

    sealed class MainActState {
        data class RetrieveToken(val token: String) : MainActState()
        data class EnteredFullScreen(val isFullScreen: Boolean) : MainActState()
        object RequireLoginState : MainActState()
        object LoginSuccessState : MainActState()
        object LoginErrorState : MainActState()
    }

    sealed class NotificationState {
        data class NavigateToPodcastPush(val podcastId: String, val liveVideo: String?) :
            NotificationState()

        object NotificationOpened : NotificationState()
        data class RequestNotificationPermission(val permission: String) : NotificationState()
        object NotificationGranted : NotificationState()
        object NotificationRevoked : NotificationState()
    }

    val actState = MutableLiveData<MainActState>()
    val notificationState = MutableLiveData<NotificationState>()

    fun updateState(mainActState: MainActState) {
        actState.value = mainActState
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
        val permission = if (Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
        }
        val permissionStatus = ContextCompat.checkSelfPermission(
            getApplication<Application>().applicationContext,
            permission
        ) != PackageManager.PERMISSION_GRANTED
        if (!permissionStatus) {
            notificationState.value = NotificationState.RequestNotificationPermission(permission)
        } else {
            updateNotificationPermission(permissionStatus)
        }
    }

    fun checkToken() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = firebaseService.generateFirebaseToken()
            if (token.isSuccess) {
                preferencesService.editPreference(TOKEN_PREFERENCES, token.success.data)
                updateUserToken(token.success.data)
            }
        }
    }

    private fun updateUserToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = usersService.currentUser()
            user?.let {
                val storedUser = usersService.getSingleData(it.uid)
                if (storedUser.isSuccess) {
                    (storedUser.success.data as User).apply {
                        this.token = token
                    }
                    usersService.editData(storedUser.success.data)
                }
                when (storedUser) {
                    is ServiceResult.Error -> {
                        Log.e(javaClass.simpleName, "updateUser: Error updating user info")
                    }
                    is ServiceResult.Success -> {

                    }
                }
            }
        }
    }

    fun validatePush(podcastExtra: String?, videoExtra: String?) {
        podcastExtra?.let {
            Log.i(javaClass.simpleName, "validatePush: payload -> ${podcastExtra} $videoExtra")
            notificationState.value =
                NotificationState.NavigateToPodcastPush(podcastExtra, videoExtra)
        }
    }

    fun notificationOpen() {
        notificationState.value = NotificationState.NotificationOpened
    }

    fun checkUser() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            updateState(MainActState.RequireLoginState)
        } else {
            updateState(MainActState.LoginSuccessState)
        }
    }

}