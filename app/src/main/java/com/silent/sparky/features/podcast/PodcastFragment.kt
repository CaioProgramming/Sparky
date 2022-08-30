package com.silent.sparky.features.podcast

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.extensions.gone
import com.silent.core.component.GroupType
import com.silent.core.component.HostGroup
import com.silent.core.component.HostGroupAdapter
import com.silent.core.component.showError
import com.silent.core.podcast.HeaderType
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastHeader
import com.silent.core.utils.ImageUtils
import com.silent.core.utils.WebUtils
import com.silent.core.videos.Video
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentPodcastBinding
import com.silent.sparky.features.home.adapter.VideoHeaderAdapter
import com.silent.sparky.features.live.data.LiveHeader
import com.silent.sparky.features.live.data.VideoMedia
import com.silent.sparky.features.podcast.schedule.TodayLiveDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.NumberFormat

class PodcastFragment : Fragment() {
    private var podcastFragmentBinding: FragmentPodcastBinding? = null
    private val podcastViewModel by viewModel<PodcastViewModel>()
    private val args by navArgs<PodcastFragmentArgs>()
    private var program: Podcast? = null

    private fun onSelectHeader(podcastHeader: PodcastHeader) {
        findNavController().navigate(
            R.id.action_podcastFragment_to_playlistFragment,
            bundleOf("header" to podcastHeader)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        podcastFragmentBinding = FragmentPodcastBinding.inflate(inflater)
        return podcastFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        delayedFunction {
            podcastViewModel.getPodcastData(args.podcastId, args.liveVideo)
        }
        podcastFragmentBinding?.run {
            errorView.run {
                errorAnimation.setAnimationFromUrl("https://assets3.lottiefiles.com/packages/lf20_txli4cbw.json")
            }
        }
    }

    private fun animateSubscriberCount(count: Int) {
        val animator = ValueAnimator()
        animator.run {
            setObjectValues(0, count)
            addUpdateListener {
                podcastFragmentBinding?.subscriberCount?.text =
                    NumberFormat.getInstance().format(it.animatedValue.toString().toInt())
            }
            duration = 10000
            start()
        }

    }

    private fun setupPodcast(
        podcast: Podcast,
        headers: ArrayList<PodcastHeader>,
        isFavorite: Boolean
    ) {
        program = podcast
        podcastFragmentBinding?.run {
            loading.fadeOut()
            (activity as AppCompatActivity?)?.run {
                setSupportActionBar(programToolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                programToolbar.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
            programName.text = podcast.name
            Glide.with(requireContext()).load(podcast.iconURL).into(programIcon)
            Glide.with(requireContext()).load(podcast.cover)
                .error(ImageUtils.getNotificationIcon(podcast.notificationIcon).drawable)
                .into(podcastCover)
            programIcon.setOnLongClickListener { _ ->
                WebUtils(requireContext()).openYoutubeChannel(podcast.youtubeID)
                false
            }
            val hostHeaders = ArrayList<HostGroup>()
            if (podcast.hosts.isNotEmpty()) {
                hostHeaders.add(HostGroup("Apresentado por", podcast.hosts))
            }

            hostsRecyclerView.adapter = HostGroupAdapter(hostHeaders, false, { host, groupType ->
                when (groupType) {
                    GroupType.HOSTS -> {
                        if (host.socialUrl.isNotEmpty()) {
                            WebUtils(requireContext()).openInstagram(host.socialUrl)
                        }
                    }
                }
            }, podcast.highLightColor)
            animateSubscriberCount(podcast.subscribe)
            setupHeaders(headers)
            podcastLiveProgress.rimColor = Color.parseColor(podcast.highLightColor)
            podcastLiveProgress.progress = 100
            favoritePodcast.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(podcast.highLightColor))
            favoritePodcast.isChecked = isFavorite
            favoritePodcast.setOnCheckedChangeListener { buttonView, isChecked ->
                podcastViewModel.favoritePodcast(podcast.id, isChecked)
            }
            podcastSearch.setOnSearchClickListener {
                podcastViewModel.searchEpisodesAndCuts(podcast, podcastSearch.query.toString())
            }
            podcastSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { podcastViewModel.searchEpisodesAndCuts(podcast, it) }
                        ?: podcastViewModel.getPodcastData(args.podcastId)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
            val closeButton: View? =
                podcastSearch.findViewById(androidx.appcompat.R.id.search_close_btn)
            closeButton?.setOnClickListener {
                podcastSearch.setQuery("", false)
                podcastViewModel.getPodcastData(args.podcastId)
            }
            appBar.slideInBottom()
            mainContent.slideInBottom()
        }
    }

    private fun navigateToLive(
        podcast: Podcast,
        video: Video,
        isLive: Boolean,
        type: HeaderType = HeaderType.VIDEOS
    ) {
        val mediaType = if (isLive) VideoMedia.LIVE else {
            when (type) {
                HeaderType.VIDEOS -> VideoMedia.EPISODE
                HeaderType.CUTS -> VideoMedia.CUT
                else -> VideoMedia.EPISODE
            }
        }
        val liveHeader =
            LiveHeader(
                podcast,
                video,
                mediaType
            )
        val bundle = bundleOf("live_object" to liveHeader)
        findNavController().navigate(R.id.action_podcastFragment_to_liveFragment, bundle)
    }

    private fun FragmentPodcastBinding.showLoading() {
        loading.fadeIn()
        appBar.fadeOut()
        mainContent.gone()
    }


    private fun observeViewModel() {
        podcastViewModel.podcastState.observe(viewLifecycleOwner) {
            when (it) {
                is PodcastViewModel.PodcastState.PodcastDataRetrieved -> {
                    setupPodcast(it.podcast, it.headers, it.isFavorite)
                }
                PodcastViewModel.PodcastState.PodcastFailedState -> {
                    podcastFragmentBinding?.errorView?.showError("Ocorreu um erro ao obter os dados desse podcast") {
                        findNavController().popBackStack()
                    }
                }
                is PodcastViewModel.PodcastState.RetrieveSearch -> {
                    setupHeaders(it.headers)
                }
                else -> {}
            }
        }
        podcastViewModel.scheduleState.observe(viewLifecycleOwner) {
            when (it) {
                is PodcastViewModel.ScheduleState.TodayGuestState -> {
                    program?.let { podcast ->
                        TodayLiveDialog(
                            requireContext(),
                            podcast.iconURL,
                            Color.parseColor(podcast.highLightColor),
                            it.video
                        ) { video ->
                            program?.let { podcast ->
                                navigateToLive(podcast, video, true)
                            }
                        }.buildDialog()
                        podcastFragmentBinding?.run {
                            podcastLiveProgress.indeterminate = true
                            programIcon.setOnClickListener { _ ->
                                navigateToLive(podcast, it.video, true)
                            }
                        }
                    }

                }
            }
        }
        podcastViewModel.viewModelState.observe(viewLifecycleOwner) {
            when (it) {
                ViewModelBaseState.LoadingState -> {
                    podcastFragmentBinding?.showLoading()
                }
                is ViewModelBaseState.ErrorState -> {
                    podcastFragmentBinding?.errorView?.showError(it.dataException.code.message) {
                        findNavController().popBackStack()
                    }
                }
                else -> {}
            }
        }
    }

    private fun setupHeaders(headers: ArrayList<PodcastHeader>) {
        podcastFragmentBinding?.channelVideos?.adapter =
            VideoHeaderAdapter(headers, ::onSelectHeader, onVideoClick = { video, podcast, header ->
                navigateToLive(podcast, video, false, header.type)
            })
    }


}