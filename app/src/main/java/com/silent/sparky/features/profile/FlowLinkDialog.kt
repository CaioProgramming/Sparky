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
import com.silent.sparky.features.profile.viewmodel.ProfileState
import com.silent.sparky.features.profile.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.flow_link_alert.*
import kotlinx.android.synthetic.main.flow_link_alert.view.*
import kotlinx.android.synthetic.main.profile_card.*

class FlowLinkDialog(val user: User) : BottomSheetDialogFragment() {


    private val profileViewModel: ProfileViewModel by lazy {
        ViewModelProvider(this)[ProfileViewModel::class.java]
    }

    private fun View.configure() {
        flow_account_name.editText?.addTextChangedListener {
            flow_account_name.error = ""
            if (search_account.isGone) {
                if (it.isNullOrEmpty()) {
                    search_account.gone()
                } else {
                    search_account.slideInBottom()
                }
            }
        }
        search_account.setOnClickListener {
            flow_account_name.editText?.let {
                profileViewModel.getFlowProfile(it.text.toString())
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
                    flow_account_name.error = "Usuário não encontrado, tente novamente"

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

    private fun setupFlowUser(flowProfile: FlowProfile) {
        flow_profile.fadeIn()
        save_link_button.fadeIn()
        Glide.with(requireContext())
            .load(flowProfile.profile_picture)
            .error(ImageUtils.getRandomIcon())
            .into(profile_pic)
        username.text = flowProfile.username
        realName.text = flowProfile.bio
        save_link_button.setOnClickListener {
            save_link_button.text = ""
            loading.fadeIn()
            user.flowUserName = flowProfile.username
            user.profilePic = flowProfile.profile_picture
            profileViewModel.editData(user)
        }
    }


}