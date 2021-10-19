package com.silent.sparky.program

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silent.core.program.Program
import com.silent.core.twitch.ChannelResource
import com.silent.core.youtube.PlaylistResource
import com.silent.core.youtube.YoutubeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProgramViewModel: ViewModel() {

    private val youtubeService = YoutubeService()

    sealed class ChannelState {
        object ChannelFailedState : ChannelState()
        data class ChannelUploadsRetrieved(val videos:  List<PlaylistResource>, val playlistId: String): ChannelState()
        data class ChannelCutsRetrieved(val videos:  List<PlaylistResource>, val playlistId: String): ChannelState()
        data class ChannelDataRetrieved(val channelDetails: ChannelResource): ChannelState()
    }

   val channelState = MutableLiveData<ChannelState>()

    fun getChannelData(program: Program) {
        GlobalScope.launch(Dispatchers.IO) {
            val channelsResponse = youtubeService.getChannelDetails(program.youtubeID)
            print("Channel Data -> $channelsResponse")
            channelState.postValue(ChannelState.ChannelDataRetrieved(channelsResponse.items[0]))
        }
    }

    fun getChannelCuts(cutsID: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val channelUploads = youtubeService.getChannelUploads(cutsID)
                print("Videos data -> $channelUploads" )
                channelState.postValue(ChannelState.ChannelCutsRetrieved(channelUploads.items, cutsID))
            } catch (e: Exception) {
                e.printStackTrace()
                channelState.postValue(ChannelState.ChannelFailedState)
            }
        }
    }

    fun getChannelVideos(playlistId: String, onGetVideos: ((List<PlaylistResource>) -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val channelUploads = youtubeService.getChannelUploads(playlistId)
                print("Videos data -> $channelUploads" )
                channelState.postValue(ChannelState.ChannelUploadsRetrieved(channelUploads.items, playlistId))
                onGetVideos?.invoke(channelUploads.items)
            } catch (e: Exception) {
                e.printStackTrace()
                channelState.postValue(ChannelState.ChannelFailedState)
            }
        }
    }

}