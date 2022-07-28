package com.silent.sparky.features.home

import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.common.util.CollectionUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import com.silent.sparky.features.podcast.di.podcastModule
import com.silent.sparky.features.profile.di.profileModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class HomeActivity : AuthActivity() {

    private val mainActViewModel: MainActViewModel by viewModel()
    private lateinit var notificationPermissionRequest: ActivityResultLauncher<String>
    private val podcastExtra: String? by lazy {
        intent.extras?.getString("podcast")
    }
    private val videoExtra: String? by lazy {
        intent.extras?.getString("video")
    }
    var homeBinding: ActivityHomeBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                mainActViewModel.updateNotificationPermission(it)
            }
        loadKoinModules(listOf(homeModule, profileModule, podcastModule, cutsModule))
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding?.root)
        homeBinding?.run {
            val navView: BottomNavigationView = navView
            val navController = findNavController(R.id.nav_host_fragment_activity_home)
            navView.setupWithNavController(navController)
            if (mainActViewModel.actState.value is MainActViewModel.MainActState.NotificationOpenedState) {
                navController.navigate(R.id.navigation_home)
            }
        }
        observeViewModel()
        mainActViewModel.checkNotifications()
        mainActViewModel.validatePush(podcastExtra, videoExtra)
        mainActViewModel.checkUser()
    }


    private fun observeViewModel() {
        mainActViewModel.actState.observe(this) {
            when(it) {
                MainActViewModel.MainActState.RequireLoginState -> login()
                is MainActViewModel.MainActState.RetrieveToken -> {
                    if (BuildConfig.DEBUG) {
                        getView().showSnackBar(
                            "FCM Token retrieved ${it.token}",
                            backColor = ContextCompat.getColor(this, WARNING_COLOR)
                        )
                    }
                }
                else -> {}
            }
        }

        mainActViewModel.notificationState.observe(this) {
            when(it) {
                MainActViewModel.NotificationState.RequestNotification -> {
                    notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {}
            }
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