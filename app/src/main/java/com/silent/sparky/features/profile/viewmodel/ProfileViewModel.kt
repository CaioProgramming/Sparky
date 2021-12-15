package com.silent.sparky.features.profile.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.flow.FlowService
import com.silent.core.users.User
import com.silent.core.users.UsersService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ViewModelBaseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : BaseViewModel<User>() {
    private val flowService = FlowService()
    override val service = UsersService()
    val profileState = MutableLiveData<ProfileState>()


    fun findUser() {
        try {
            query(currentUser!!.uid, "uid")
        } catch (e: Exception) {
            e.printStackTrace()
            viewModelState.postValue(ViewModelBaseState.RequireAuth)
        }
    }

    fun getFlowProfile(username: String = "lilfall") {
        val user = if (username.isEmpty()) "lilfall" else username
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val flowProfile = flowService.getProfile(user)
                profileState.postValue(ProfileState.FlowUserRetrieve(flowProfile.profile))
            } catch (e: Exception) {
                e.printStackTrace()
                viewModelState.postValue(ViewModelBaseState.ErrorState(DataException()))
            }
        }

    }


}