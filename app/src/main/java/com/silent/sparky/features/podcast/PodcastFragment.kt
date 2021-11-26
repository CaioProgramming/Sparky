package com.silent.sparky.features.podcast

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.animations.slideInRight
import com.silent.core.program.Podcast
import com.silent.core.utils.WebUtils
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.ilustriscore.core.utilities.gone
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.ilustriscore.core.utilities.visible
import com.silent.sparky.R
import com.silent.sparky.data.PodcastHeader
import com.silent.sparky.features.home.adapter.VideoHeaderAdapter
import com.silent.sparky.features.podcast.adapter.HostAdapter
import kotlinx.android.synthetic.main.fragment_podcast.*

class PodcastFragment : Fragment() {
    private val programViewModel = PodcastViewModel()
    private val args by navArgs<PodcastFragmentArgs>()
    private val channelSectionsAdapter = VideoHeaderAdapter(ArrayList(), ::onSelectHeader)
    private var program: Podcast? = null

    private fun onSelectHeader(podcastHeader: PodcastHeader) {
        WebUtils(requireContext()).openYoutubePlaylist(podcastHeader.playlistId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_podcast, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.program_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.setSupportActionBar(program_toolbar)
        observeViewModel()
        programViewModel.getChannelData(args.podcastId)
    }

    private fun setupPodcast(podcast: Podcast) {
        program_name.text = podcast.name
        Glide.with(this).load(podcast.iconURL).into(program_icon)
        program_icon.setOnLongClickListener { _ ->
            WebUtils(requireContext()).openYoutubeChannel(podcast.youtubeID)
            false
        }
        channel_videos.adapter = channelSectionsAdapter
        if (podcast.hosts.isNotEmpty()) {
            hosts_title.visible()
            hosts_recycler_view.adapter = HostAdapter(podcast.hosts)
        } else {
            hosts_title.gone()
        }
        program = podcast
        loading.fadeOut()
        delayedFunction {
            app_bar.fadeIn()
            channel_videos.slideInRight()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.twitch_link -> {
                program?.let {
                    WebUtils(requireContext()).openTwitch(it.twitch)
                }
                return false
            }
            R.id.twitter_link -> {
                program?.let {
                    WebUtils(requireContext()).openTwitter(it.twitter)
                }
                return false
            }
            R.id.instagram_link -> {
                program?.let {
                    WebUtils(requireContext()).openInstagram(it.instagram)
                }
               return false
            }
            else -> return false
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
                    error_view.fadeIn()
                    loading.fadeOut()
                }
                else -> {

                }
            }
        })
    }

    companion object {
        private const val PROGRAM = "PROGRAM"
        fun getLaunchIntent(podcast: Podcast, context: Context) {
            Intent(context, PodcastFragment::class.java).apply {
                putExtra(PROGRAM, podcast)
                context.startActivity(this)
            }
        }
    }
}