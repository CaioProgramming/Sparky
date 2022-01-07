package com.silent.sparky.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ilustris.animations.fadeIn
import com.ilustris.animations.slideInBottom
import com.silent.core.flow.data.FlowProfile
import com.silent.core.users.User
import com.silent.core.utils.ImageUtils
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.gone
import com.silent.sparky.R
import com.silent.sparky.databinding.FlowLinkAlertBinding
import com.silent.sparky.features.profile.viewmodel.ProfileState
import com.silent.sparky.features.profile.viewmodel.ProfileViewModel

class FlowLinkDialog(val user: User) : BottomSheetDialogFragment() {

    private var flowLinkAlertBinding: FlowLinkAlertBinding? = null

    private val profileViewModel: ProfileViewModel by lazy {
        ViewModelProvider(this)[ProfileViewModel::class.java]
    }

    private fun View.configure() {
        flowLinkAlertBinding = FlowLinkAlertBinding.bind(this).apply {
            flowAccountName.editText?.addTextChangedListener {
                flowAccountName.error = ""
                if (searchAccount.isGone) {
                    if (it.isNullOrEmpty()) {
                        searchAccount.gone()
                    } else {
                        searchAccount.slideInBottom()
                    }
                }
            }
            searchAccount.setOnClickListener {
                flowAccountName.editText?.let {
                    profileViewModel.getFlowProfile(it.text.toString())
                }
            }
        }

        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_Sparky)
        return inflater.inflate(R.layout.flow_link_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.configure()
    }


    private fun observeViewModel() {
        profileViewModel.profileState.observe(this, {
            when (it) {
                is ProfileState.FlowUserRetrieve -> {
                    setupFlowUser(it.flowProfile)
                }
                ProfileState.FlowUserError -> {
                    flowLinkAlertBinding?.flowAccountName?.error =
                        "Usuário não encontrado, tente novamente"

                }
            }
        })
        profileViewModel.viewModelState.observe(this, {
            when (it) {
                is ViewModelBaseState.DataUpdateState -> {
                    dismiss()
                }
            }
        })
    }

    private fun setupFlowUser(fProfile: FlowProfile) {
        flowLinkAlertBinding?.run {
            flowProfile.root.fadeIn()
            flowLinkAlertBinding?.saveLinkButton?.fadeIn()
            Glide.with(requireContext())
                .load(fProfile.profile_picture)
                .error(ImageUtils.getRandomIcon())
                .into(flowProfile.profilePic)
            flowProfile.username.text = fProfile.username
            flowProfile.realName.text = fProfile.bio
            saveLinkButton.setOnClickListener {
                saveLinkButton.text = ""
                loading.fadeIn()
                user.flowUserName = fProfile.username
                user.profilePic = fProfile.profile_picture
                profileViewModel.editData(user)
            }
        }

    }


}