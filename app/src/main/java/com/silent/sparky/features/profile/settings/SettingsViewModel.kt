package com.silent.sparky.features.profile.settings

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.PodcastService
import com.silent.core.podcast.podcasts
import com.silent.core.preferences.PreferencesService
import com.silent.core.users.User
import com.silent.core.users.UsersService
import com.silent.core.utils.PODCASTS_PREFERENCES
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.model.ViewModelBaseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : BaseViewModel<User>(application) {

    sealed class SettingsState {
        data class PodcastsPreferencesRetrieve(val podcasts: podcasts) : SettingsState()
    }

    override val service = UsersService()
    private val podcastsService = PodcastService()
    private val preferencesService = PreferencesService(application)
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
            val favoritePodcasts =
                preferencesService.getStringSetValue(PODCASTS_PREFERENCES) ?: emptyList()
            val favoriteList = ArrayList(favoritePodcasts)
            favoriteList.remove(id)
            val result =
                preferencesService.editPreference(PODCASTS_PREFERENCES, favoriteList.toSet())
            when (result) {
                is ServiceResult.Error -> {
                    viewModelState.postValue(ViewModelBaseState.ErrorState(result.errorException))
                }
                is ServiceResult.Success -> {
                    loadSettings()
                }
            }

        }
    }


}