package com.silent.manager.features.newpodcast.fragments.hosts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.silent.core.instagram.InstagramUserResponse
import com.silent.manager.R
import com.silent.manager.features.newpodcast.NewPodcastViewModel
import com.silent.manager.states.HostState
import kotlinx.android.synthetic.main.host_insta_dialog.*

class HostInstagramDialog : BottomSheetDialogFragment() {

    lateinit var onInstagramRetrieve: (InstagramUserResponse) -> Unit

    private val newPodcastViewModel: NewPodcastViewModel by lazy {
        ViewModelProvider(requireActivity())[NewPodcastViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.host_insta_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        confirm_insta_button.setOnClickListener {
            loading.fadeIn()
            username_input_layout.fadeOut()
            confirm_insta_button.fadeOut()
            newPodcastViewModel.getInstagramData(username_editText.text.toString())
        }
    }

    private fun observeViewModel() {
        newPodcastViewModel.hostState.observe(this, {
            when (it) {
                is HostState.HostInstagramRetrieve -> {
                    onInstagramRetrieve(it.instagramUser)
                    dialog?.dismiss()
                }
                HostState.ErrorFetchInstagram -> {
                    loading.fadeOut()
                    username_input_layout.fadeIn()
                    confirm_insta_button.fadeIn()
                    username_input_layout.error = "Erro ao encontrar usuário, tente novamente."
                }
                else -> {

                }
            }
        })
    }

    companion object {

        fun getInstance(onInstaRetrieve: (InstagramUserResponse) -> Unit): HostInstagramDialog {
            return HostInstagramDialog().apply {
                onInstagramRetrieve = onInstaRetrieve
            }
        }

    }

}