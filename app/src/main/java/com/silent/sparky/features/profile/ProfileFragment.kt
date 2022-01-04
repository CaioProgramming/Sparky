package com.silent.sparky.features.profile

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.ilustris.animations.*
import com.silent.core.flow.data.FlowProfile
import com.silent.core.users.User
import com.silent.core.utils.ImageUtils
import com.silent.ilustriscore.core.model.ErrorType
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.RC_SIGN_IN
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.sparky.R
import com.silent.sparky.features.profile.viewmodel.ProfileState
import com.silent.sparky.features.profile.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_card.*
import java.text.NumberFormat

class ProfileFragment : Fragment() {

    val viewModel = ProfileViewModel()
    var flowDialog: FlowLinkDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        viewModel.findUser()
    }

    private fun observeViewModel() {
        viewModel.viewModelState.observe(this, {
            when (it) {
                ViewModelBaseState.RequireAuth -> {

                }
                ViewModelBaseState.DataDeletedState -> {
                }
                is ViewModelBaseState.DataRetrievedState -> {

                }
                is ViewModelBaseState.DataListRetrievedState -> {
                    val user = it.dataList[0] as User
                    setupUser(user)
                    viewModel.getFlowProfile(user.flowUserName)
                }
                is ViewModelBaseState.DataSavedState -> {
                    viewModel.findUser()
                }
                is ViewModelBaseState.DataUpdateState -> {
                    flowDialog?.dismiss()
                    viewModel.findUser()
                }
                is ViewModelBaseState.FileUploadedState -> {

                }
                is ViewModelBaseState.ErrorState -> {
                    if (it.dataException.code == ErrorType.NOT_FOUND) {
                        viewModel.saveFirebaseUser()
                    } else {
                        view?.showSnackBar("Ocorreu um erro inesperado", backColor = Color.RED)
                    }
                }
                ViewModelBaseState.LoadingState -> {
                    if (loading.isGone) {
                        loading.popIn()
                        profile_appbar.fadeOut()
                        user_badges.fadeOut()
                    }
                }
            }
        })
        viewModel.profileState.observe(this, {
            when (it) {
                is ProfileState.FlowUserRetrieve -> {
                    setupFlowProfile(it.flowProfile)
                }
                ProfileState.FlowUserError -> {
                    link_flow.text = "Vincular conta flow"

                }
            }
        })
    }

    private fun setupUser(user: User) {
        loading.popOut()
        userNameTitle.text = user.name
        username.text = user.name
        Glide.with(requireContext())
            .load(user.profilePic)
            .error(ImageUtils.getRandomIcon())
            .into(profile_pic)
        profile_appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            run {
                val percentage = Math.abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
                userNameTitle.alpha = percentage
                profile_toolbar.alpha = percentage
            }
        })
        link_flow.setOnClickListener {
            flowDialog = FlowLinkDialog(user)
            flowDialog?.show(requireActivity().supportFragmentManager, "FLOWLINKDIALOG")
        }
        profile_appbar.slideInRight()

    }

    private fun setupFlowProfile(flowProfile: FlowProfile) {
        username.text = flowProfile.username
        userNameTitle.text = flowProfile.username
        link_flow.text = "Alterar conta flow"
        user_badges.adapter = BadgeAdapter(flowProfile.selected_badges)
        user_badges.slideInBottom()
        animateBadgeCount(flowProfile.selected_badges.size)
    }

    private fun animateBadgeCount(count: Int) {
        val animator = ValueAnimator()
        animator.run {
            setObjectValues(0, count)
            addUpdateListener {
                badges_count.text =
                    NumberFormat.getInstance().format(it.animatedValue.toString().toInt())
            }
            duration = (100 * count).toLong()
            start()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            viewModel.findUser()
        }
    }


}