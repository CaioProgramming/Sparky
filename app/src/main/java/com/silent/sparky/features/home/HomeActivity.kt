package com.silent.sparky.features.home

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.common.util.CollectionUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilustris.ui.auth.AuthActivity
import com.ilustris.ui.auth.LoginHelper
import com.ilustris.ui.extensions.ERROR_COLOR
import com.ilustris.ui.extensions.getView
import com.ilustris.ui.extensions.showSnackBar
import com.silent.sparky.R
import com.silent.sparky.databinding.ActivityHomeBinding
import com.silent.sparky.di.appModule
import com.silent.sparky.features.cuts.di.cutsModule
import com.silent.sparky.features.home.di.homeModule
import com.silent.sparky.features.home.viewmodel.MainActViewModel
import com.silent.sparky.features.podcast.di.podcastModule
import com.silent.sparky.features.profile.di.profileModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class HomeActivity : AuthActivity() {

    private val mainActViewModel : MainActViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(listOf(appModule, homeModule, profileModule, podcastModule, cutsModule))
        val homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        val navView: BottomNavigationView = homeBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )*/
        observeViewModel()
        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(listOf(appModule, homeModule, profileModule, podcastModule, cutsModule))
    }

    private fun observeViewModel() {
        mainActViewModel.actState.observe(this) {
            when(it) {
                MainActViewModel.MainActState.RequireLoginState -> login()
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