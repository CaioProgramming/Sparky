package com.silent.sparky.features.profile.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.flow.FlowService
import com.silent.core.users.User
import com.silent.core.users.UsersService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ErrorType
import com.silent.ilustriscore.core.model.ViewModelBaseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : BaseViewModel<User>(application) {
    private val flowService = FlowService()
    override val service = UsersService()
    val profileState = MutableLiveData<ProfileState>()


    fun findUser() {
        try {
            query(getUser()!!.uid, "id")
        } catch (e: Exception) {
            e.printStackTrace()
            viewModelState.postValue(ViewModelBaseState.RequireAuth)
        }
    }

    fun getFlowProfile(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val flowProfile = flowService.getProfile(username)
                profileState.postValue(ProfileState.FlowUserRetrieve(flowProfile.profile))
            } catch (e: Exception) {
                e.printStackTrace()
                profileState.postValue(ProfileState.FlowUserError)
            }
        }

    }

    fun saveFirebaseUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = User().apply {
                    name = getUser()!!.displayName.toString()
                    profilePic = getUser()!!.photoUrl.toString()
                    id = getUser()!!.uid
                    uid = getUser()!!.uid
                }
                editData(user)
            } catch (e: Exception) {
                e.printStackTrace()
                viewModelState.postValue(ViewModelBaseState.ErrorState(DataException(ErrorType.SAVE)))
            }
        }


    }


}