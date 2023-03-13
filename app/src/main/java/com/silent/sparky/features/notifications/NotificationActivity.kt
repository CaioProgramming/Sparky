package com.silent.sparky.features.notifications

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ilustris.animations.fadeIn
import com.silent.core.users.User
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.sparky.databinding.ActivityNotificationBinding
import com.silent.sparky.features.home.HomeActivity
import com.silent.sparky.features.notifications.adapter.NotificationGroupAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationActivity : AppCompatActivity() {

    private val podcastExtra: String? by lazy {
        intent.extras?.getString("podcast")
    }
    private val videoExtra: String? by lazy {
        intent.extras?.getString("video")
    }
    private var activityNotificationBinding: ActivityNotificationBinding? = null
    private val notificationViewModel by viewModel<NotificationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNotificationBinding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(activityNotificationBinding?.root)
        notificationViewModel.fetchNotifications()
        observeViewModel()
    }

    private fun observeViewModel() {
        notificationViewModel.notificationState.observe(this) {
            when (it) {
                NotificationViewModel.NotificationState.NotificationFetchError -> showError()
                is NotificationViewModel.NotificationState.NotificationsFetched -> setupNotifications(
                    it.user,
                    it.notificationsGroups
                )
                NotificationViewModel.NotificationState.EmptyNotifications -> showError()
                is NotificationViewModel.NotificationState.RedirectNotification -> redirectNotification(
                    it.podcastId,
                    it.videoId
                )
            }
        }
        notificationViewModel.viewModelState.observe(this) {
            when (it) {
                is ViewModelBaseState.DataUpdateState -> notificationViewModel.fetchNotifications()
                else -> {

                }
            }
        }
    }

    private fun stopLoading() {
        activityNotificationBinding?.root?.hideShimmer()
        activityNotificationBinding?.root?.stopShimmer()
    }

    private fun showError() {
        stopLoading()
        activityNotificationBinding?.errorView?.run {
            root.fadeIn()
            errorMessage.text = "Você não possui notificações de seus podcasts favoritos"
            errorButton.setOnClickListener {
                finish()
            }
            errorButton.text = "Voltar"
        }
    }
    private fun setupNotifications(user: User, notificationsGroups: List<NotificationGroup>) {
        stopLoading()
        activityNotificationBinding?.run {
            setSupportActionBar(notificationsToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            notificationsToolbar.setNavigationOnClickListener {
                this@NotificationActivity.finish()
            }
            notificationsRecycler.adapter = NotificationGroupAdapter(notificationsGroups) {
                notificationViewModel.openNotification(user, it)
            }
        }
    }

    private fun redirectNotification(podcastId: String?, videoId: String?) {
        podcastId?.let {
            val homeIntent = Intent(this, HomeActivity::class.java).apply {
                putExtra("podcast", podcastId)
                putExtra("video", videoId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(homeIntent)
        }
    }
}