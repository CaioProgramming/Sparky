package com.silent.manager.features.newpodcast.fragments.hosts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.silent.core.component.GroupType
import com.silent.core.instagram.InstagramUserResponse
import com.silent.manager.R
import com.silent.manager.databinding.HostInstaDialogBinding
import com.silent.manager.states.HostState

class HostInstagramDialog : BottomSheetDialogFragment() {

    private lateinit var group: GroupType
    lateinit var onInstagramRetrieve: (InstagramUserResponse) -> Unit

    private val instagramHostViewModel = InstagramHostViewModel()
    private val hostInstaDialogBinding by lazy {
        view?.let { HostInstaDialogBinding.bind(it) }
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
        hostInstaDialogBinding?.run {
            usernameInputLayout.helperText =
                if (group == GroupType.HOSTS) requireContext().getString(R.string.host_hint) else requireContext().getString(
                    R.string.guest_hint
                )
            usernameEditText.hint =
                if (group == GroupType.HOSTS) "Nome do Host" else "Nome do convidado"
            confirmInstaButton.setOnClickListener {
                loading.fadeIn()
                usernameInputLayout.fadeOut()
                confirmInstaButton.fadeOut()
                instagramHostViewModel.getInstagramData(usernameEditText.text.toString())
            }
            continueAnywayInstaButton.setOnClickListener {
                onInstagramRetrieve(InstagramUserResponse(usernameEditText.text.toString(), "", "", ""))
            }
        }

    }

    private fun observeViewModel() {
        instagramHostViewModel.hostState.observe(this) {
            when (it) {
                is HostState.HostInstagramRetrieve -> {
                    onInstagramRetrieve(it.instagramUser)
                    dismiss()
                }
                HostState.ErrorFetchInstagram -> {
                    hostInstaDialogBinding?.showError()
                }
            }
        }
    }

    private fun HostInstaDialogBinding.showError() {
        loading.fadeOut()
        usernameInputLayout.fadeIn()
        confirmInstaButton.fadeIn()
        usernameInputLayout.error = "Erro ao encontrar usuário, tente novamente."
    }

    companion object {

        fun getInstance(
            groupType: GroupType = GroupType.HOSTS,
            onInstaRetrieve: (InstagramUserResponse) -> Unit
        ): HostInstagramDialog {
            return HostInstagramDialog().apply {
                onInstagramRetrieve = onInstaRetrieve
                this.group = groupType
            }
        }

    }

}