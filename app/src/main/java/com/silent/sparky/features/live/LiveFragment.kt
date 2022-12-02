package com.silent.sparky.features.live

import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.extensions.gone
import com.ilustris.ui.extensions.visible
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.silent.core.podcast.HeaderType
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.PodcastHeader
import com.silent.core.utils.ImageUtils
import com.silent.core.utils.WebUtils
import com.silent.core.videos.Video
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.sparky.R
import com.silent.sparky.databinding.PodcastLiveFragmentBinding
import com.silent.sparky.features.home.adapter.VideoHeaderAdapter
import com.silent.sparky.features.home.viewmodel.MainActViewModel
import com.silent.sparky.features.live.components.SparkyPlayerController
import com.silent.sparky.features.live.data.LiveHeader
import com.silent.sparky.features.live.data.VideoMedia
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.NumberFormat


class LiveFragment : Fragment() {


    private var liveYouTubePlayer: YouTubePlayer? = null
    private var podcastLiveFragmentBinding: PodcastLiveFragmentBinding? = null
    private val args by navArgs<LiveFragmentArgs>()
    private val liveViewModel: LiveViewModel by viewModel()
    private val mainActViewModel: MainActViewModel by sharedViewModel()
    private lateinit var live: LiveHeader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        podcastLiveFragmentBinding = PodcastLiveFragmentBinding.inflate(inflater)
        return podcastLiveFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        podcastLiveFragmentBinding = PodcastLiveFragmentBinding.bind(view)
        setupView()
        observeViewModel()
    }

    private fun PodcastLiveFragmentBinding.startLoading() {
        liveShimmer.showShimmer(true)
    }

    private fun PodcastLiveFragmentBinding.stopLoading() {
        delayedFunction(2000) {
            liveShimmer.stopShimmer()
            liveShimmer.hideShimmer()
        }
    }

    private fun setupView() {
        podcastLiveFragmentBinding?.run {
            live = args.liveObject
            initializePlayer(
                Color.parseColor(live.podcast.highLightColor),
                live.video.id,
                ImageUtils.getNotificationIcon(live.podcast.notificationIcon).drawable,
                args.liveObject.isLiveVideo
            )
            setupPodcast(live.podcast)
            setupVideoInfo(live)
            collapseButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }


    private fun PodcastLiveFragmentBinding.setupVideoInfo(liveHeader: LiveHeader) {
        liveTitle.text = liveHeader.video.title
        liveDescription.text = liveHeader.video.description
        collapseButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        liveViewModel.liveState.observe(viewLifecycleOwner) {
            when (it) {
                is LiveViewModel.LiveState.RelatedVideosRetrieved -> podcastLiveFragmentBinding?.setupRelatedVideos(
                    it.headers
                )
            }
        }
        liveViewModel.videoTitleState.observe(viewLifecycleOwner) {
            when (it) {
                is LiveViewModel.VideoTitleState.UpdateTitleStyle -> podcastLiveFragmentBinding?.run {
                    liveTitle.text = Html.fromHtml(it.newTitle)
                }
            }
        }
    }

    private fun PodcastLiveFragmentBinding.setupRelatedVideos(header: List<PodcastHeader>) {
        relatedVideos.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        relatedVideos.adapter = VideoHeaderAdapter(
            ArrayList(header),
            headerSelected = { it.playlistId?.let { id -> openPodcast(id) } },
            onVideoClick = { video, podcast, header ->
                val type =
                    if (header.type == HeaderType.VIDEOS) VideoMedia.EPISODE else VideoMedia.CUT
                startLoading()
                loadVideo(video, podcast, type)
                shareButton.setOnClickListener {
                    val message = getShareMessage(podcast.name, args.liveObject.type, video.id)
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message)
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                }
            }, selectPodcast = { openPodcast(it.id) })
        relatedVideos.slideInBottom()
        liveViewModel.formatCoHostName(
            args.liveObject.video.title,
            args.liveObject.podcast.highLightColor
        )
        stopLoading()
    }

    private fun openPodcast(podcastId: String) {
        val bundle = bundleOf("podcast_id" to podcastId, "live_video" to null)
        findNavController().navigate(R.id.action_liveFragment_to_podcastFragment, bundle)
    }

    private fun PodcastLiveFragmentBinding.setupPodcast(podcast: Podcast) {
        val highlightColor = Color.parseColor(podcast.highLightColor)
        val shimmerBuilder = Shimmer.ColorHighlightBuilder()
            .setBaseColor(ContextCompat.getColor(root.context, R.color.md_grey900))
            .setHighlightColor(highlightColor)
            .setDuration(1500)
            .setIntensity(0.4f)
            .setDropoff(0.9f)
            .setBaseAlpha(0.3f)
            .setHighlightAlpha(0.5f)
        val shimmer = shimmerBuilder.build()
        liveShimmer.setShimmer(shimmer)
        podcastName.text = podcast.name
        gradientBackground.backgroundTintList = ColorStateList.valueOf(highlightColor)
        Glide.with(requireContext()).load(podcast.iconURL).into(podcastIcon)
        podcastName.setOnClickListener {
            openPodcast(podcast.id)
        }
        podcastIcon.setOnClickListener {
            openPodcast(podcast.id)
        }
        podcastSubscribers.contentDescription = "${podcast.subscribe} inscritos."
        val animator = ValueAnimator()
        animator.run {
            setObjectValues(0, podcast.subscribe)
            addUpdateListener {
                podcastSubscribers.text =
                    NumberFormat.getInstance()
                        .format(it.animatedValue.toString().toInt()) + " inscritos"
            }
            duration = 2000
            start()
        }
        podcastIcon.borderColor = Color.parseColor(podcast.highLightColor)
        shareButton.setOnClickListener {
            val message =
                getShareMessage(podcast.name, args.liveObject.type, args.liveObject.video.youtubeID)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, message)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        when (args.liveObject.type) {
            VideoMedia.LIVE -> {
                shareButton.contentDescription = "Compartilhar live"
                shareButton.tooltipText = "Compartilhar live"
            }
            VideoMedia.EPISODE -> {
                shareButton.contentDescription = "Compartilhar episódio"
                shareButton.tooltipText = "Compartilhar episódio"
            }
            VideoMedia.CUT -> {
                shareButton.contentDescription = "Compartilhar corte"
                shareButton.tooltipText = "Compartilhar corte"
            }
        }
        liveViewModel.getRelatedVideos(
            args.liveObject.video,
            podcast,
            args.liveObject.type
        )
    }

    private fun getShareMessage(podcastName: String, media: VideoMedia, videoId: String): String {
        val youtubeLink = WebUtils(requireContext()).getYoutubeLink(videoId)
        return when (media) {
            VideoMedia.LIVE -> "Ta rolando live do $podcastName, da uma olhada!\n$youtubeLink"
            VideoMedia.EPISODE -> "Olha só esse episódio do $podcastName!\n$youtubeLink"
            VideoMedia.CUT -> "Da uma olhada nesse corte do $podcastName!\n$youtubeLink"
        }
    }

    private fun loadVideo(
        video: Video,
        podcast: Podcast,
        media: VideoMedia = args.liveObject.type
    ) {
        live.video.title = video.title
        live.video.description = video.description
        liveYouTubePlayer?.loadVideo(video.id, 0f)
        liveViewModel.getRelatedVideos(video, podcast, media)
        podcastLiveFragmentBinding?.setupVideoInfo(live)
    }

    private fun PodcastLiveFragmentBinding.initializePlayer(
        highlightColor: Int,
        videoId: String,
        podcastIcon: Int,
        isLive: Boolean
    ) {
        val playerUi = livePlayer.inflateCustomPlayerUi(R.layout.custom_player_layout)
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                liveYouTubePlayer = youTubePlayer
                val customplayerUiController = SparkyPlayerController(
                    playerUi,
                    livePlayer,
                    youTubePlayer,
                    highlightColor,
                    podcastIcon,
                    videoId,
                    isLive
                ) { isFullScreen ->
                    enterFullScreen(isFullScreen)
                }
                youTubePlayer.addListener(customplayerUiController)
            }
        }
        val options: IFramePlayerOptions = IFramePlayerOptions.Builder().controls(0).build()
        livePlayer.initialize(listener, false, options)
        livePlayer.enableBackgroundPlayback(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        podcastLiveFragmentBinding?.livePlayer?.release()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun PodcastLiveFragmentBinding.enterFullScreen(isFullScreen: Boolean) {
        val orientation =
            if (isFullScreen) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requireActivity().requestedOrientation = orientation
        if (isFullScreen) {
            liveCard.strokeColor = Color.TRANSPARENT
            liveTop.gone()
            liveBottom.gone()
            liveCard.layoutParams = liveCard.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            livePlayer.layoutParams = livePlayer.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        } else {
            liveCard.strokeColor = requireContext().getColor(R.color.md_grey900)
            liveCard.layoutParams = liveCard.layoutParams.apply {
                width =
                    requireContext().resources.getDimensionPixelOffset(R.dimen.player_card_portrait_width)
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            livePlayer.layoutParams = livePlayer.layoutParams.apply {
                width =
                    requireContext().resources.getDimensionPixelOffset(R.dimen.player_portrait_width)
                height =
                    requireContext().resources.getDimensionPixelOffset(R.dimen.player_portrait_height)
            }
            liveTop.visible()
            liveBottom.visible()
        }
        mainActViewModel.updateState(MainActViewModel.MainActState.EnteredFullScreen(isFullScreen))
    }

}