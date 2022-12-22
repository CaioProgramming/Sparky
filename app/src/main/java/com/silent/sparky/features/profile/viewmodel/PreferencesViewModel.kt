package com.silent.sparky.features.profile.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastService
import com.silent.core.podcast.podcasts
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PreferencesViewModel(
    application: Application,
    override val service: PodcastService
) : BaseViewModel<Podcast>(application) {

    sealed class PreferencesViewState {
        data class PodcastAdded(val podcast: Podcast) : PreferencesViewState()
        data class PodcastRemoved(val podcast: Podcast) : PreferencesViewState()
        data class PodcastPreferencesRetrieved(val favorites: List<Podcast>) :
            PreferencesViewState()

        data class PodcastsRetrieved(val podcasts: podcasts) : PreferencesViewState()
        object PreferencesSaved : PreferencesViewState()
        object PreferencesError : PreferencesViewState()
    }

    var preferencesViewState = MutableLiveData<PreferencesViewState>()

    fun savePodcastPreferences(selectedPodcasts: ArrayList<Podcast>) {
        viewModelScope.launch {
            try {
                selectedPodcasts.forEach {
                    it.subscribers.add(getUser()!!.uid)
                    editData(it)
                }
                preferencesViewState.postValue(PreferencesViewState.PreferencesSaved)
            } catch (e: Exception) {
                e.printStackTrace()
                preferencesViewState.postValue(PreferencesViewState.PreferencesError)
            }
        }
    }
    override fun getAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val podcasts = service.getAllData().success.data as podcasts
                val sortedPodcasts =
                    podcasts.sortedByDescending { it.subscribers.contains(getUser()!!.uid) }
                        .sortedByDescending { it.subscribe }
                preferencesViewState.postValue(
                    PreferencesViewState.PodcastsRetrieved(
                        ArrayList(
                            sortedPodcasts
                        )
                    )
                )
                preferencesViewState.postValue(
                    PreferencesViewState.PodcastPreferencesRetrieved(
                        podcasts.filter { it.subscribers.contains(getUser()!!.uid) })
                )
            } catch (e: Exception) {
                e.printStackTrace()
                sendErrorState(DataException.UNKNOWN)
            }

        }
    }
}