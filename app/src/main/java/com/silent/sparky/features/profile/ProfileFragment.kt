package com.silent.sparky.features.profile

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.animations.popIn
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.extensions.ERROR_COLOR
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.component.showError
import com.silent.core.flow.data.FlowProfile
import com.silent.core.stickers.response.Badge
import com.silent.core.users.User
import com.silent.ilustriscore.core.model.ErrorType
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentProfileBinding
import com.silent.sparky.features.home.viewmodel.MainActViewModel
import com.silent.sparky.features.profile.adapter.BadgeAdapter
import com.silent.sparky.features.profile.dialog.FlowLinkDialog
import com.silent.sparky.features.profile.viewmodel.ProfileState
import com.silent.sparky.features.profile.viewmodel.ProfileViewModel
import com.silent.sparky.features.profile.viewmodel.StickersState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.NumberFormat

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModel()
    private val mainActViewModel: MainActViewModel by sharedViewModel()

    private var profileBinding: FragmentProfileBinding? = null

    private var flowDialog: FlowLinkDialog? = null
    private var user: User? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileBinding = FragmentProfileBinding.inflate(inflater)
        return profileBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        profileBinding?.errorView?.errorAnimation?.setAnimationFromUrl("https://assets10.lottiefiles.com/packages/lf20_rjobbdq9.json")
        viewModel.findUser()
    }

    private fun login() {
        mainActViewModel.updateState(MainActViewModel.MainActState.RequireLoginState)
    }


    private fun observeViewModel() {
        viewModel.viewModelState.observe(viewLifecycleOwner) {
            when (it) {
                ViewModelBaseState.RequireAuth -> {
                    login()
                }
                is ViewModelBaseState.DataListRetrievedState -> {
                    user = it.dataList[0] as User
                    user?.let { findUser ->
                        setupUser(findUser)
                        viewModel.getFlowProfile(findUser.flowUserName)
                    }

                }
                is ViewModelBaseState.DataSavedState -> {
                    viewModel.findUser()
                }
                is ViewModelBaseState.DataUpdateState -> {
                    flowDialog?.dismiss()
                    viewModel.findUser()
                }
                is ViewModelBaseState.ErrorState -> {
                    when (it.dataException.code) {
                        ErrorType.NOT_FOUND -> {
                            viewModel.saveFirebaseUser()
                        }
                        ErrorType.AUTH -> {
                            profileBinding?.errorView?.showError("Ocorreu um erro inesperado(${it.dataException.code.message}") {
                                login()
                            }
                        }
                        else -> {
                            profileBinding?.errorView?.showError("Ocorreu um erro inesperado(${it.dataException.code.message}") {
                                viewModel.findUser()
                            }
                        }
                    }
                }
                ViewModelBaseState.LoadingState -> {
                    profileBinding?.run {
                        loading.fadeIn()
                        profileAppbar.fadeOut()
                        userBadges.fadeOut()
                    }

                }

                ViewModelBaseState.LoadCompleteState -> profileBinding?.loadComplete()
                else -> {}
            }
        }
        viewModel.profileState.observe(viewLifecycleOwner) {
            when (it) {
                is ProfileState.FlowUserRetrieve -> {
                    setupFlowProfile(it.flowProfile)
                }
                ProfileState.FlowUserError -> {
                    profileBinding?.run {
                        badgesErrorView.showError("Vincule sua conta flow para ver seus emblemas aqui.") {
                            flowLinkButton.callOnClick()
                        }
                        flowLinkButton.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.material_grey500
                            )
                        )
                    }


                }
            }
        }
        viewModel.stickersState.observe(viewLifecycleOwner) {
            when (it) {
                StickersState.ErrorFetchingStickers -> {
                    requireView().showSnackBar(
                        "Ocorreu um erro ao obter os emblemas",
                        backColor = ContextCompat.getColor(requireContext(), ERROR_COLOR)
                    )
                }
                StickersState.FetchingStickers -> {
                    profileBinding?.loadingBadges?.popIn()
                }
                is StickersState.StickersRetrieved -> {
                    profileBinding?.setupStickers(it.badges)
                }
            }
        }
        mainActViewModel.actState.observe(viewLifecycleOwner) {
            when (it) {
                MainActViewModel.MainActState.LoginErrorState -> {
                    profileBinding?.errorView?.showError("Ocorreu um erro ao realizar o login, tente novamente") {
                        login()
                    }
                }
                MainActViewModel.MainActState.LoginSuccessState -> {
                    viewModel.findUser()
                }
                else -> {}
            }
        }
    }

    private fun FragmentProfileBinding.setupStickers(badges: List<Badge>) {
        animateBadgeCount(badges.size)
        userBadges.adapter = BadgeAdapter(badges)
        loadingBadges.fadeOut()
    }

    private fun setupUser(user: User) {
        profileBinding?.run {
            userBio.text = user.name
            userNameTitle.text = user.name
            Glide.with(requireContext())
                .load(user.profilePic)
                .error(R.drawable.ic_iconmonstr_connection_1)
                .into(profilePic)
            profileAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                run {
                    val percentage =
                        Math.abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange
                    userNameTitle.alpha = percentage
                    profileToolbar.alpha = percentage
                }
            })
            flowLinkButton.setOnClickListener {
                flowDialog = FlowLinkDialog(user)
                flowDialog?.show(requireActivity().supportFragmentManager, "FLOWLINKDIALOG")
            }
            settingsButton.setOnClickListener {
                val bundle = bundleOf("user_object" to user)
                findNavController().navigate(
                    R.id.action_navigation_profile_to_settingsFragment,
                    bundle
                )
            }
            usernameCard.fadeIn()
            realName.fadeIn()
            loading.fadeOut()
        }
    }

    private fun FragmentProfileBinding.loadComplete() {
        profileAppbar.slideInBottom()
        userBadges.fadeIn()
    }

    private fun setupFlowProfile(flowProfile: FlowProfile) {
        profileBinding?.run {
            username.text = flowProfile.username
            userNameTitle.text = flowProfile.username
            realName.text = user?.name
            userBio.text = flowProfile.bio
            profileBinding?.flowLinkButton?.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.material_yellow700
                )
            )
            badgesLayout.slideInBottom()
            usernameCard.fadeIn()
        }
        viewModel.getBadges(flowProfile.username)
    }

    private fun animateBadgeCount(count: Int) {
        val animator = ValueAnimator()
        animator.run {
            setObjectValues(0, count)
            addUpdateListener {
                profileBinding?.badgesCount?.text =
                    NumberFormat.getInstance().format(it.animatedValue.toString().toInt())
            }
            duration = (100 * count).toLong()
            start()
        }

    }


}