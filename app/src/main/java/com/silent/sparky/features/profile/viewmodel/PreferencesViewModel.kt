package com.silent.sparky.features.profile.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.firebase.FirebaseService
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.preferences.PreferencesService
import com.silent.core.utils.PODCASTS_PREFERENCES
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PreferencesViewModel(
    application: Application,
    override val service: PodcastService,
    private val preferencesService: PreferencesService,
    private val firebaseService: FirebaseService
) : BaseViewModel<Podcast>(application) {

    sealed class PreferencesViewState {
        data class PodcastAdded(val podcast: String) : PreferencesViewState()
        data class PodcastRemoved(val podcast: String) : PreferencesViewState()
        data class PodcastPreferencesRetrieved(val favorites: List<String>) : PreferencesViewState()
        object PreferencesSaved : PreferencesViewState()
        object PreferencesError : PreferencesViewState()
    }

    var preferencesViewState = MutableLiveData<PreferencesViewState>()


    fun savePodcastPreferences(selectedPodcasts: ArrayList<String>) {
        viewModelScope.launch {
            try {
                val preferedPodcasts = ArrayList<String>()
                selectedPodcasts.forEach {
                    preferedPodcasts.add(it)
                    firebaseService.subscribeToTopic(it, ::handleServiceResult)
                }
                preferencesService.editPreference(PODCASTS_PREFERENCES, preferedPodcasts.toSet())
                preferencesViewState.postValue(PreferencesViewState.PreferencesSaved)
            } catch (e: Exception) {
                e.printStackTrace()
                preferencesViewState.postValue(PreferencesViewState.PreferencesError)
            }
        }
    }

    fun getPodcastPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            val preferences = preferencesService.getStringSetValue(PODCASTS_PREFERENCES)
            preferences?.let {
                preferencesViewState.postValue(PreferencesViewState.PodcastPreferencesRetrieved(it.toList()))
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
}