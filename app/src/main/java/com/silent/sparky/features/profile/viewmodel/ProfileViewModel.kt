package com.silent.sparky.features.profile.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silent.core.flow.FlowService
import com.silent.core.stickers.StickersService
import com.silent.core.users.User
import com.silent.core.users.UsersService
import com.silent.ilustriscore.core.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    application: Application,
    override val service: UsersService,
    private val flowService: FlowService,
    private val stickersService: StickersService
) : BaseViewModel<User>(application) {
    val profileState = MutableLiveData<ProfileState>()
    val stickersState = MutableLiveData<StickersState>()

    fun findUser() {
        try {
            query(getUser()!!.uid, "id")
        } catch (e: Exception) {
            e.printStackTrace()
            sendErrorState(DataException.AUTH)
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
            } finally {
                updateViewState(ViewModelBaseState.LoadCompleteState)
            }
        }

    }

     fun getBadges(username: String) {
        stickersState.postValue(StickersState.FetchingStickers)
        viewModelScope.launch(Dispatchers.IO) {
            try {
               val stickersResponse = stickersService.getUserStickers(username)
                Log.i(javaClass.simpleName, "getBadges: $stickersResponse")
                stickersState.postValue(StickersState.StickersRetrieved(stickersResponse.badges))
            } catch (e: Exception) {
                e.printStackTrace()
                stickersState.postValue(StickersState.ErrorFetchingStickers)
            }
        }
    }

    fun saveFirebaseUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val authUser = getUser()!!
                val user = User().apply {
                    name = authUser.displayName.toString()
                    profilePic = authUser.photoUrl.toString()
                    id = authUser.uid
                    uid = authUser.uid
                }
                editData(user)
            } catch (e: Exception) {
                e.printStackTrace()
                viewModelState.postValue(ViewModelBaseState.ErrorState(DataException(ErrorType.SAVE)))
            }
        }


    }


}