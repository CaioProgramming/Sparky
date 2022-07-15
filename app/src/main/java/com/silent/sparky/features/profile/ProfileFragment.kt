package com.silent.sparky.features.profile

import android.animation.ValueAnimator
import android.graphics.Color
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
import com.silent.core.flow.data.FlowProfile
import com.silent.core.stickers.response.Badge
import com.silent.core.users.User
import com.silent.core.utils.ImageUtils
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
    private lateinit var user: User
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
                is ViewModelBaseState.ErrorState -> {
                    when (it.dataException.code) {
                        ErrorType.NOT_FOUND -> {
                            viewModel.saveFirebaseUser()
                        }
                        ErrorType.AUTH -> {
                            login()
                        }
                        else -> {
                            view?.showSnackBar(
                                "Ocorreu um erro inesperado(${it.dataException.code.message}",
                                backColor = Color.RED
                            )
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
            }
        }
        viewModel.profileState.observe(viewLifecycleOwner) {
            when (it) {
                is ProfileState.FlowUserRetrieve -> {
                    setupFlowProfile(it.flowProfile)
                }
                ProfileState.FlowUserError -> {
                    requireView().showSnackBar("Ocorreu um erro ao encontrar a conta Flow", backColor = ContextCompat.getColor(requireContext(), ERROR_COLOR))
                    profileBinding?.linkFlow?.text = "Vincular conta flow"

                }
            }
        }
        viewModel.stickersState.observe(viewLifecycleOwner) {
            when(it) {
                StickersState.ErrorFetchingStickers -> {
                    requireView().showSnackBar(
                        "Ocorreu um erro ao obter os stickers",
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
                    requireView().showSnackBar(
                        "Ocorreu um erro ao realizar o login, tente novamente.",
                        ContextCompat.getColor(requireContext(), ERROR_COLOR),
                        action = {
                            login()
                        })
                }
                MainActViewModel.MainActState.LoginSuccessState -> {
                    viewModel.findUser()
                }
            }
        }
    }

    private fun FragmentProfileBinding.setupStickers(badges: List<Badge>) {
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
            linkFlow.setOnClickListener {
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
            realName.text = user.name
            userBio.text = flowProfile.bio
            linkFlow.text = "Alterar conta flow"
            badgesLayout.slideInBottom()
            usernameCard.fadeIn()
        }
        animateBadgeCount(flowProfile.total_badges)
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