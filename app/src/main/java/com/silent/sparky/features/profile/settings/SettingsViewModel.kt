package com.silent.sparky.features.profile.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.silent.core.firebase.FirebaseService
import com.silent.core.podcast.PodcastService
import com.silent.core.podcast.podcasts
import com.silent.core.preferences.PreferencesService
import com.silent.core.users.User
import com.silent.core.users.UsersService
import com.silent.core.utils.PODCASTS_PREFERENCES
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.model.ViewModelBaseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application,
    override val service: UsersService,
    private val preferencesService: PreferencesService,
    private val firebaseService: FirebaseService
) : BaseViewModel<User>(application) {

    sealed class SettingsState {
        data class PodcastsPreferencesRetrieve(val podcasts: podcasts) : SettingsState()
        object UserSignedOut: SettingsState()
    }

    private val podcastsService = PodcastService()
    val settingsState = MutableLiveData<SettingsState>()

    fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val favoritePodcasts =
                preferencesService.getStringSetValue(PODCASTS_PREFERENCES) ?: emptyList()
            when (val request = podcastsService.getAllData()) {
                is ServiceResult.Error -> {
                    viewModelState.postValue(ViewModelBaseState.ErrorState(request.errorException))
                }
                is ServiceResult.Success -> {
                    val podcasts = request.data as podcasts
                    val filteredPodcasts = podcasts.filter { favoritePodcasts.contains(it.id) }
                    settingsState.postValue(
                        SettingsState.PodcastsPreferencesRetrieve(
                            ArrayList(
                                filteredPodcasts.sortedByDescending { it.subscribe })
                        )
                    )
                }
            }
        }
    }

    fun removeFavorite(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val favoritePodcasts = preferencesService.getStringSetValue(PODCASTS_PREFERENCES) ?: emptyList()
            val favoriteList = ArrayList(favoritePodcasts)
            favoriteList.remove(id)
            when (val result = preferencesService.editPreference(PODCASTS_PREFERENCES, favoriteList.toSet())) {
                is ServiceResult.Error -> {
                    viewModelState.postValue(ViewModelBaseState.ErrorState(result.errorException))
                }
                is ServiceResult.Success -> {
                    firebaseService.unsubscribeTopic(id, ::handleServiceResult)
                    loadSettings()
                }
            }

        }
    }
    private fun handleServiceResult(serviceResult: ServiceResult<DataException, String>) {
        when(serviceResult) {
            is ServiceResult.Error -> {
                Log.e(javaClass.simpleName, "handleServiceResult: Error -> ${serviceResult.errorException}", )
            }
            is ServiceResult.Success -> {
                Log.i(javaClass.simpleName, "handleServiceResult: Success -> ${serviceResult.data}")
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        if (getUser() == null) {
            settingsState.postValue(SettingsState.UserSignedOut)
        }
    }

}