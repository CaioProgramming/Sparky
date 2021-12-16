package com.silent.sparky.features.profile

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ilustris.animations.popOut
import com.ilustris.animations.slideInBottom
import com.ilustris.animations.slideInRight
import com.silent.core.flow.data.FlowProfile
import com.silent.core.users.User
import com.silent.core.utils.ImageUtils
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.RC_SIGN_IN
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.sparky.R
import com.silent.sparky.features.profile.viewmodel.ProfileState
import com.silent.sparky.features.profile.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.NumberFormat

class ProfileFragment : Fragment() {

    val viewModel = ProfileViewModel()

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

                }
                is ViewModelBaseState.DataUpdateState -> {

                }
                is ViewModelBaseState.FileUploadedState -> {

                }
                is ViewModelBaseState.ErrorState -> {
                    view?.showSnackBar("Ocorreu um erro inesperado", backColor = Color.RED)
                }
            }
        })
        viewModel.profileState.observe(this, {
            when (it) {
                is ProfileState.FlowUserRetrieve -> {
                    setupFlowProfile(it.flowProfile)
                }
            }
        })
    }

    private fun setupUser(user: User) {
        name.text = user.name
        Glide.with(requireContext())
            .load(user.profilePic)
            .error(ImageUtils.getRandomIcon())
            .into(profile_image)
    }

    private fun setupFlowProfile(flowProfile: FlowProfile) {
        loading.popOut()
        user_name.text = flowProfile.username
        profile_appbar.slideInRight()
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