package com.silent.sparky.features.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainActViewModel(application: Application) : AndroidViewModel(application) {

    sealed class MainActState {
        object RequireLoginState: MainActState()
        object LoginSuccessState: MainActState()
        object LoginErrorState: MainActState()
    }

    val actState = MutableLiveData<MainActState>()

    fun updateState(mainActState: MainActState) {
        actState.postValue(mainActState)
    }

}