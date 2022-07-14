package com.silent.sparky.features.home.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.firebase.FirebaseService
import com.silent.core.preferences.PreferencesService
import com.silent.core.utils.TOKEN_PREFERENCES
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActViewModel(
    application: Application,
    val firebaseService: FirebaseService,
    val preferencesService: PreferencesService
) : AndroidViewModel(application) {

    sealed class MainActState {
        data class RetrieveToken(val token: String) : MainActState()
        object RequireLoginState : MainActState()
        object LoginSuccessState : MainActState()
        object LoginErrorState : MainActState()
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
        } else {
            notificationState.value = NotificationState.NotificationRevoked
        }
    }

    fun checkNotifications() {
        val permissionStatus = ContextCompat.checkSelfPermission(
            getApplication<Application>().applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
        if (!permissionStatus) {
            notificationState.value = NotificationState.RequestNotification
        } else {
            updateNotificationPermission(permissionStatus)
        }
    }

    fun checkToken() {
        viewModelScope.launch(Dispatchers.IO) {
            when(val token = firebaseService.generateFirebaseToken()) {
                is ServiceResult.Error -> {
                    Log.e(this@MainActViewModel.javaClass.simpleName, "checkToken: Error getting token \n ${token.errorException} ")
                }
                is ServiceResult.Success -> {
                    preferencesService.editPreference(TOKEN_PREFERENCES, token.data)
                }
            }
        }
    }

}