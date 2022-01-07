package com.silent.sparky.features.podcast

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.animations.slideInRight
import com.silent.core.component.HostAdapter
import com.silent.core.podcast.Podcast
import com.silent.core.utils.WebUtils
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.ilustriscore.core.utilities.gone
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.ilustriscore.core.utilities.visible
import com.silent.sparky.data.PodcastHeader
import com.silent.sparky.databinding.FragmentPodcastBinding
import com.silent.sparky.features.home.adapter.VideoHeaderAdapter
import java.text.NumberFormat

class PodcastFragment : Fragment() {
    private var podcastFragmentBinding: FragmentPodcastBinding? = null
    private val programViewModel = PodcastViewModel()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        podcastFragmentBinding?.run {
            (activity as AppCompatActivity?)?.setSupportActionBar(programToolbar)

        }
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
            programName.text = podcast.name
            if (podcast.highLightColor.isNotEmpty()) {
                programIcon.borderColor = Color.parseColor(podcast.highLightColor)
            }
            Glide.with(requireContext()).load(podcast.iconURL).into(programIcon)
            programIcon.setOnLongClickListener { _ ->
                WebUtils(requireContext()).openYoutubeChannel(podcast.youtubeID)
                false
            }
            channelVideos.adapter = channelSectionsAdapter
            if (podcast.hosts.isNotEmpty()) {
                hostsTitle.visible()
                hostsRecyclerView.adapter = HostAdapter(
                    podcast.hosts, highLightColor = podcast.highLightColor, isEdit = false,
                    hostSelected = {
                        WebUtils(requireContext()).openInstagram(it.user)
                    },
                )
            } else {
                hostsTitle.gone()
            }
            loading.fadeOut()
            delayedFunction {
                appBar.fadeIn()
                channelVideos.slideInRight()
            }
            animateSubscriberCount(podcast.subscribe)
        }

    }

    private fun observeViewModel() {
        programViewModel.channelState.observe(this, {
            when (it) {
                is PodcastViewModel.ChannelState.ChannelDataRetrieved -> {
                    setupPodcast(it.podcast)
                    channelSectionsAdapter.updateSection(
                        it.uploads
                    )
                    channelSectionsAdapter.updateSection(
                        it.cuts
                    )
                }
                PodcastViewModel.ChannelState.ChannelFailedState -> {
                    view?.showSnackBar("Ocorreu um erro ao obter os vídeos")
                    podcastFragmentBinding?.loading?.fadeOut()
                }
                is PodcastViewModel.ChannelState.ChannelHostRetrieved -> {
                }
            }
        })
    }
}