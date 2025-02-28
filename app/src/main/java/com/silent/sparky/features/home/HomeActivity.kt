package com.silent.sparky.features.home

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.common.util.CollectionUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.auth.AuthActivity
import com.ilustris.ui.extensions.ERROR_COLOR
import com.ilustris.ui.extensions.WARNING_COLOR
import com.ilustris.ui.extensions.getView
import com.ilustris.ui.extensions.showSnackBar
import com.silent.sparky.BuildConfig
import com.silent.sparky.R
import com.silent.sparky.databinding.ActivityHomeBinding
import com.silent.sparky.features.cuts.di.cutsModule
import com.silent.sparky.features.home.di.homeModule
import com.silent.sparky.features.home.viewmodel.MainActViewModel
import com.silent.sparky.features.live.di.liveModule
import com.silent.sparky.features.podcast.di.podcastModule
import com.silent.sparky.features.profile.di.profileModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class HomeActivity : AuthActivity() {

    private val mainActViewModel: MainActViewModel by viewModel()
    private lateinit var notificationPermissionRequest: ActivityResultLauncher<String>
    var homeBinding: ActivityHomeBinding? = null
    private val podcastExtra: String? by lazy {
        intent.extras?.getSerializable("podcast") as String?
    }
    private val videoExtra: String? by lazy {
        intent.extras?.getString("video")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                mainActViewModel.updateNotificationPermission(it)
            }
        loadKoinModules(listOf(homeModule, profileModule, podcastModule, cutsModule, liveModule))
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding?.root)
        homeBinding?.run {
            val navView: BottomNavigationView = navView
            val navController = findNavController(R.id.nav_host_fragment_activity_home)
            navView.setupWithNavController(navController)
        }
        observeViewModel()
        mainActViewModel.checkNotifications()
        mainActViewModel.validatePush(podcastExtra, videoExtra)
        mainActViewModel.checkUser()
    }


    private fun observeViewModel() {
        mainActViewModel.actState.observe(this) {
            when (it) {
                MainActViewModel.MainActState.RequireLoginState -> login()
                is MainActViewModel.MainActState.RetrieveToken -> {
                    if (BuildConfig.DEBUG) {
                        getView().showSnackBar(
                            "FCM Token retrieved ${it.token}",
                            backColor = ContextCompat.getColor(this, WARNING_COLOR)
                        )
                    }
                }
                is MainActViewModel.MainActState.EnteredFullScreen -> {
                    if (it.isFullScreen) {
                        homeBinding?.hideBottom()
                    } else {
                        homeBinding?.showBottom()
                    }
                }
                else -> {}
            }
        }

        mainActViewModel.notificationState.observe(this) {
            when (it) {
                is MainActViewModel.NotificationState.RequestNotificationPermission -> {
                    notificationPermissionRequest.launch(it.permission)
                }
                else -> {}
            }
        }
    }

    private fun ActivityHomeBinding.hideBottom() {
        navView.fadeOut()
        gradientFade.fadeOut()
        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private fun ActivityHomeBinding.showBottom() {
        navView.slideInBottom()
        gradientFade.fadeIn()
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    private fun login() {
        val providers = CollectionUtils.listOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        launchLogin(R.mipmap.ic_launcher, R.style.Ilustris_Theme, providers)
    }

    override fun onLoginResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == Activity.RESULT_OK && result.idpResponse != null) {
            mainActViewModel.updateState(MainActViewModel.MainActState.LoginSuccessState)
            mainActViewModel.checkToken()
        } else {
            getView().showSnackBar(
                "Ocorreu um erro ao realizar o login, tente novamente",
                actionText = "Ok", action = {
                    login()
                }, backColor = getColor(ERROR_COLOR)
            )

        }
    }

}