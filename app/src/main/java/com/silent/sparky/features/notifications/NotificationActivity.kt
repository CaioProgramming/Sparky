package com.silent.sparky.features.notifications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.ilustris.animations.fadeIn
import com.silent.core.podcast.Podcast
import com.silent.core.videos.Video
import com.silent.ilustriscore.core.utilities.delayedFunction
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
                    it.notificationsGroups
                )
                NotificationViewModel.NotificationState.EmptyNotifications -> showError()
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

    private fun setupNotifications(notificationsGroups: List<NotificationGroup>) {
        stopLoading()
        activityNotificationBinding?.run {
            podcastExtra?.let {
                parseNotification { podcast, video ->
                    redirectNotification(podcast, video)
                }
            }
            setSupportActionBar(notificationsToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            notificationsToolbar.setNavigationOnClickListener {
                this@NotificationActivity.finish()
            }
            notificationsRecycler.adapter = NotificationGroupAdapter(notificationsGroups) {
                //redirectNotification(it.podcast, it.video)
            }
        }

    }

    private fun parseNotification(parseCallback: (Podcast?, Video?) -> Unit) {
        Log.i(
            javaClass.simpleName,
            "validatePush: \n podcastObject -> ${podcastExtra}\nVideoObject -> $videoExtra"
        )
        val podcast: Podcast? = podcastExtra?.let { Gson().fromJson(it, Podcast::class.java) }
        val video: Video? = videoExtra?.let { Gson().fromJson(it, Video::class.java) }
        parseCallback(podcast, video)
    }

    private fun redirectNotification(podcast: Podcast?, video: Video?) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("podcast", podcast)
            putExtra("video", video)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        delayedFunction(1500) {
            startActivity(homeIntent)
        }
    }
}