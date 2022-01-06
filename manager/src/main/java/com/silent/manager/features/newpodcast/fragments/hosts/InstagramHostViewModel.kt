package com.silent.manager.features.newpodcast.fragments.hosts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silent.core.instagram.InstagramService
import com.silent.manager.states.HostState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InstagramHostViewModel : ViewModel() {

    private val instagramService = InstagramService()
    val hostState = MutableLiveData<HostState>()
    fun getInstagramData(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val instagramResponse = instagramService.getUserInfo(userName)
                hostState.postValue(HostState.HostInstagramRetrieve(instagramResponse.graphql.user))
            } catch (e: Exception) {
                e.printStackTrace()
                hostState.postValue(HostState.ErrorFetchInstagram)
            }
        }
    }

}