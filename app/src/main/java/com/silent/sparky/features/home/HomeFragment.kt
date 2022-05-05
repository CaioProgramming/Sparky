package com.silent.sparky.features.home

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts
import com.silent.core.utils.WebUtils
import com.silent.core.videos.Video
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.RC_SIGN_IN
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.navigation.ModuleNavigator
import com.silent.navigation.NavigationUtils
import com.silent.sparky.R
import com.silent.sparky.data.PodcastHeader
import com.silent.sparky.databinding.HomeFragmentBinding
import com.silent.sparky.features.home.adapter.ProgramsAdapter
import com.silent.sparky.features.home.adapter.VideoHeaderAdapter
import com.silent.sparky.features.home.data.LiveHeader
import com.silent.sparky.features.home.viewmodel.HomeState
import com.silent.sparky.features.home.viewmodel.HomeViewModel
import com.silent.sparky.features.profile.dialog.PreferencesDialogFragment

class HomeFragment : Fragment() {

    var homeFragmentBinding: HomeFragmentBinding? = null
    private val homeViewModel by lazy { HomeViewModel(requireActivity().application) }
    private var videoHeaderAdapter: VideoHeaderAdapter? = VideoHeaderAdapter(
        ArrayList(),
        {
            openPodcast(it.playlistId)
        },
        ::openChannel
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        setMenuVisibility(false)
        homeFragmentBinding = HomeFragmentBinding.inflate(inflater)
        return homeFragmentBinding?.root
    }

    private fun openPodcast(id: String) {
        val bundle = bundleOf("podcast_id" to id)
        findNavController().navigate(R.id.action_navigation_home_to_podcastFragment, bundle)
    }

    private fun openChannel(url: String) {
        WebUtils(requireContext()).openYoutubeChannel(url)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeFragmentBinding = HomeFragmentBinding.bind(view)
        observeViewModel()
        setupView()
    }

    private fun clearFragment() {
        homeViewModel.homeState.removeObservers(this)
        videoHeaderAdapter?.clearAdapter()
    }

    override fun onDetach() {
        clearFragment()
        super.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        homeFragmentBinding = null
    }

    private fun setupView() {
        homeFragmentBinding?.run {
            podcastsResumeRecycler.adapter = videoHeaderAdapter
            (requireActivity() as AppCompatActivity?)?.run {
                setSupportActionBar(homeToolbar)
                supportActionBar?.title = ""
            }
        }

        videoHeaderAdapter?.clearAdapter()
        homeViewModel.getHome()
    }

    private fun goToManager() {
        NavigationUtils(requireContext()).startModule(ModuleNavigator.MANAGER)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                goToManager()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            homeViewModel.getHome()
        }
    }

    private fun observeViewModel() {
        homeViewModel.homeState.observe(viewLifecycleOwner) {
            when (it) {
                is HomeState.HomeChannelRetrieved -> {
                    setupHome(it.podcastHeader)
                }
                HomeState.HomeError -> {
                    homeViewModel.getAllData()
                }

                is HomeState.HomeLivesRetrieved -> {
                    setupLive(it.podcasts)
                    //view?.showSnackBar("${it.podcasts.size} lives no momento")
                }
                HomeState.InvalidManager -> {
                    setMenuVisibility(false)
                }
                HomeState.ValidManager -> {
                    setMenuVisibility(true)
                    homeFragmentBinding?.homeAnimation?.setOnClickListener {
                        goToManager()
                    }
                }
                HomeState.PreferencesNotSet -> {
                    PreferencesDialogFragment.buildDialog(childFragmentManager) {
                        homeViewModel.getHome()
                    }
                }
            }
        }
        homeViewModel.viewModelState.observe(viewLifecycleOwner) {
            when (it) {
                ViewModelBaseState.RequireAuth -> {

                }
                is ViewModelBaseState.DataListRetrievedState -> {
                    homeFragmentBinding?.podcastsResumeRecycler?.run {
                        adapter =
                            ProgramsAdapter((it.dataList as podcasts).sortedByDescending { p -> p.subscribe }) { podcast, index ->
                                WebUtils(requireContext()).openYoutubeChannel(podcast.youtubeID)
                            }
                        layoutManager =
                            GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
                    }
                }
                is ViewModelBaseState.ErrorState -> {
                    view?.showSnackBar("Ocorreu um erro inesperado", backColor = Color.RED)
                }
            }
        }
    }

    private fun setupHome(podcastHeader: PodcastHeader) {
        videoHeaderAdapter?.updateSection(podcastHeader)
    }

    private fun setupLive(lives: ArrayList<Video>) {
        if (lives.isNotEmpty()) {
            videoHeaderAdapter?.updateSection(
                PodcastHeader(
                    "Ao vivo agora",
                    orientation = RecyclerView.HORIZONTAL,
                    seeMore = false,
                    playlistId = "",
                    videos = lives,
                    scrollAnimation = true
                )
            )
        }
    }

    private fun extractPodcasts(liveHeader: ArrayList<LiveHeader>): ArrayList<Podcast> {
        val podcasts = ArrayList<Podcast>()
        liveHeader.forEach {
            podcasts.add(it.podcast)
        }
        return podcasts
    }

}