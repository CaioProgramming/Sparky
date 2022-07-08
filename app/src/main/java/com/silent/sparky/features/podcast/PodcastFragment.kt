package com.silent.sparky.features.podcast

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ilustris.animations.*
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.component.GroupType
import com.silent.core.component.HostGroup
import com.silent.core.component.HostGroupAdapter
import com.silent.core.podcast.Host
import com.silent.core.podcast.Podcast
import com.silent.core.utils.WebUtils
import com.silent.core.videos.Video
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentPodcastBinding
import com.silent.sparky.features.home.adapter.VideoHeaderAdapter
import com.silent.sparky.features.home.data.LiveHeader
import com.silent.sparky.features.home.data.PodcastHeader
import com.silent.sparky.features.podcast.schedule.PodcastScheduleDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.NumberFormat

class PodcastFragment : Fragment() {
    private var podcastFragmentBinding: FragmentPodcastBinding? = null
    private val programViewModel by viewModel<PodcastViewModel>()
    private val args by navArgs<PodcastFragmentArgs>()
    private val channelSectionsAdapter = VideoHeaderAdapter(ArrayList(), ::onSelectHeader)
    private var program: Podcast? = null


    override fun onDestroy() {
        super.onDestroy()
        podcastFragmentBinding = null
    }

    private fun onSelectHeader(podcastHeader: PodcastHeader) {

        WebUtils(requireContext()).openYoutubePlaylist(podcastHeader.playlistId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        podcastFragmentBinding = FragmentPodcastBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return podcastFragmentBinding?.root
    }

    override fun onStart() {
        super.onStart()
        observeViewModel()
        programViewModel.getChannelData(args.podcastId)
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

    private fun setupPodcast(podcast: Podcast) {
        program = podcast
        podcastFragmentBinding?.run {
            loading.popOut()
            (activity as AppCompatActivity?)?.run {
                setSupportActionBar(programToolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                programToolbar.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
            programName.text = podcast.name
            if (podcast.highLightColor.isNotEmpty()) {
                programIcon.borderColor = Color.parseColor(podcast.highLightColor)
            }
            Glide.with(requireContext()).load(podcast.iconURL).into(programIcon)
            Glide.with(requireContext()).load(podcast.cover).into(podcastCover)
            programIcon.setOnLongClickListener { _ ->
                WebUtils(requireContext()).openYoutubeChannel(podcast.youtubeID)
                false
            }
            channelVideos.adapter = channelSectionsAdapter
            val hostHeaders = ArrayList<HostGroup>()
            if (podcast.hosts.isNotEmpty()) {
                hostHeaders.add(HostGroup("Hosts", podcast.hosts))
            }
            if (podcast.weeklyGuests.isNotEmpty()) {
                hostHeaders.add(
                    HostGroup(
                        "Próximos convidados",
                        podcast.weeklyGuests,
                        GroupType.GUESTS
                    )
                )
            }
            hostsRecyclerView.adapter = HostGroupAdapter(
                hostHeaders, false, { host, groupType ->

                    when (groupType) {
                        GroupType.HOSTS -> {
                            if (host.socialUrl.isNotEmpty()) {
                                WebUtils(requireContext()).openInstagram(host.socialUrl)
                            }
                        }
                        GroupType.GUESTS -> {
                            if (host.socialUrl.isNotEmpty()) {
                                val liveHeader = LiveHeader(podcast, host.name, host.socialUrl)
                                val bundle = bundleOf("live_object" to liveHeader)
                                findNavController().navigate(
                                    R.id.action_podcastFragment_to_liveFragment,
                                    bundle
                                )
                            }
                        }
                    }


                }, podcast.highLightColor
            )

            appBar.slideInBottom()
            channelVideos.slideInRight()
            animateSubscriberCount(podcast.subscribe)
            if (podcast.weeklyGuests.isNotEmpty()) {
                programViewModel.checkSchedule(podcast)
            }
        }
    }

    private fun navigateToLive(podcast: Podcast, host: Host) {
        val liveHeader = LiveHeader(podcast, host.name, host.socialUrl)
        val bundle = bundleOf("live_object" to liveHeader)
        findNavController().navigate(R.id.action_podcastFragment_to_liveFragment, bundle)
    }

    private fun observeViewModel() {
        programViewModel.channelState.observe(viewLifecycleOwner) {
            when (it) {
                is PodcastViewModel.PodcastState.PodcastDataRetrieved -> {
                    setupPodcast(it.podcast)
                    channelSectionsAdapter.addSections(it.headers)
                }
                PodcastViewModel.PodcastState.PodcastFailedState -> {
                    requireView().showSnackBar("Ocorreu um erro ao obter os vídeos")
                    podcastFragmentBinding?.loading?.fadeOut()
                }
                is PodcastViewModel.PodcastState.UpdateHeader -> {
                    channelSectionsAdapter.updateSection(it.position, ArrayList (it.videos), it.lastIndex)
                }
            }
        }
        programViewModel.scheduleState.observe(viewLifecycleOwner) {
            when (it) {
                is PodcastViewModel.ScheduleState.TodayGuestState -> {
                        PodcastScheduleDialog(
                            requireContext(),
                            it.position,
                            it.guests,
                            it.podcast.highLightColor
                        ) { host ->
                            navigateToLive(it.podcast, host)
                        }.buildDialog()
                }
            }
        }
    }
}