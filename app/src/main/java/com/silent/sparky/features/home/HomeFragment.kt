package com.silent.sparky.features.home

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.animations.slideInBottom
import com.ilustris.ui.auth.LoginHelper
import com.ilustris.ui.extensions.ERROR_COLOR
import com.ilustris.ui.extensions.gone
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts
import com.silent.core.utils.WebUtils
import com.silent.ilustriscore.core.model.ErrorType
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.navigation.ModuleNavigator
import com.silent.navigation.NavigationUtils
import com.silent.sparky.R
import com.silent.sparky.databinding.HomeFragmentBinding
import com.silent.sparky.features.home.adapter.PodcastsLiveAdapter
import com.silent.sparky.features.home.adapter.VideoHeaderAdapter
import com.silent.sparky.features.home.data.LiveHeader
import com.silent.sparky.features.home.data.PodcastHeader
import com.silent.sparky.features.home.viewmodel.HomeState
import com.silent.sparky.features.home.viewmodel.HomeViewModel
import com.silent.sparky.features.home.viewmodel.MainActViewModel
import com.silent.sparky.features.profile.dialog.PreferencesDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : SearchView.OnQueryTextListener, Fragment() {

    var homeFragmentBinding: HomeFragmentBinding? = null
    private val homeViewModel: HomeViewModel by viewModel()
    private val mainActViewModel by sharedViewModel<MainActViewModel>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
    }

    private fun openPodcast(id: String) {
        val bundle = bundleOf("podcast_id" to id)
        findNavController().navigate(R.id.action_navigation_home_to_podcastFragment, bundle)
    }

    private fun openChannel(url: String) {
        WebUtils(requireContext()).openYoutubeChannel(url)
    }

    override fun onStart() {
        super.onStart()
        homeViewModel.getHome()
    }

    override fun onResume() {
        super.onResume()
        setupView()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearFragment()
    }

    override fun onDetach() {
        super.onDetach()
        clearFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFragment()
    }


    private fun clearFragment() {
        homeViewModel.viewModelState.removeObservers(this)
        homeViewModel.homeState.removeObservers(this)
        homeFragmentBinding = null
    }

    private fun setupView() {
        homeFragmentBinding?.run {
            (requireActivity() as AppCompatActivity?)?.run {
                setSupportActionBar(homeToolbar)
                supportActionBar?.title = ""
            }
            homeSearch.setOnQueryTextListener(this@HomeFragment)
            homeSearch.setOnCloseListener {
                homeViewModel.getHome()
                return@setOnCloseListener false
            }
            homeSearch.setOnSearchClickListener {
                homeViewModel.searchPodcastAndEpisodes(homeSearch.query.toString())
            }
            val closeButton: View? = homeSearch.findViewById(androidx.appcompat.R.id.search_close_btn)
            closeButton?.setOnClickListener {
                homeSearch.setQuery("", false)
                homeViewModel.getHome()
            }
        }
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

    private fun observeViewModel() {
        homeViewModel.homeState.observe(viewLifecycleOwner) {
            when (it) {
                is HomeState.HomeChannelsRetrieved -> {
                    setupHome(it.podcastHeaders)
                }
                HomeState.HomeError -> {
                    homeViewModel.getAllData()
                }

                is HomeState.HomeLivesRetrieved -> {
                    setupLive(it.podcasts)
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
                HomeState.NoTokenFound -> {
                    mainActViewModel.checkToken()
                }
                HomeState.LoadingSearch -> {
                    homeFragmentBinding?.run {
                        loadingAnimation.fadeIn()
                        podcastsResumeRecycler.gone()
                    }
                }
                is HomeState.HomeSearchRetrieved -> setupHome(it.podcastHeader)
            }
        }
        homeViewModel.viewModelState.observe(viewLifecycleOwner) {
            when (it) {
                ViewModelBaseState.LoadingState -> {
                    homeFragmentBinding?.homeAnimation?.fadeIn()
                    homeFragmentBinding?.livesRecyclerView?.gone()
                }

                ViewModelBaseState.LoadCompleteState -> {
                    homeFragmentBinding?.run {
                        loadingAnimation.fadeOut()
                        appBarLayout.fadeIn()
                        podcastsResumeRecycler.fadeIn()
                        if (podcastsResumeRecycler.childCount == 0) {
                            podcastsResumeRecycler.removeAllViews()
                            homeViewModel.getHome()
                        }
                    }


                }

                ViewModelBaseState.RequireAuth -> {
                    login()
                }
                is ViewModelBaseState.DataListRetrievedState -> {
                    homeFragmentBinding?.podcastsResumeRecycler?.run {
                        adapter =
                            PodcastsLiveAdapter((it.dataList as podcasts).sortedByDescending { p -> p.subscribe }) { podcast, index ->
                                WebUtils(requireContext()).openYoutubeChannel(podcast.youtubeID)
                            }
                        layoutManager =
                            GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
                    }
                }
                is ViewModelBaseState.ErrorState -> {
                    view?.showSnackBar(
                        "Ocorreu um erro inesperado(${it.dataException.code.message}",
                        backColor = ContextCompat.getColor(requireContext(), ERROR_COLOR)
                    )
                    if (it.dataException.code == ErrorType.AUTH) {
                        mainActViewModel.updateState(MainActViewModel.MainActState.RequireLoginState)
                    }
                }
            }
        }
        mainActViewModel.actState.observe(viewLifecycleOwner) {
            if (it is MainActViewModel.MainActState.NavigateToPodcast) {
                openPodcast(it.podcastId)
                mainActViewModel.notificationOpen()
            }
        }
    }

    private fun login() {
        LoginHelper.signIn(
            requireActivity() as AppCompatActivity,
            arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build()),
            R.style.Theme_Sparky,
            R.mipmap.ic_launcher
        ) { resultCode ->
            handleResultCode(resultCode)
        }
    }

    private fun handleResultCode(resultCode: Int) {
        if (resultCode != Activity.RESULT_OK) {
            login()
        } else {
            onStart()
        }
    }

    private fun setupHome(headers: ArrayList<PodcastHeader>) {
        homeFragmentBinding?.run {
            loadingAnimation.fadeOut()
            appBarLayout.fadeIn()
            podcastsResumeRecycler.fadeIn()
            if (podcastsResumeRecycler.childCount == 0) {
                podcastsResumeRecycler.removeAllViews()
                homeViewModel.getHome()
            }
            podcastsResumeRecycler?.adapter = VideoHeaderAdapter(headers, headerSelected =  { header ->
                header.playlistId?.let { id -> openPodcast(id) }
            }, ::openChannel, selectPodcast = {
                openPodcast(it.id)
            })
        }
    }

    private fun setupLive(livePodcasts: ArrayList<Podcast>) {
        homeFragmentBinding?.livesRecyclerView?.run {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = PodcastsLiveAdapter(livePodcasts, true) { podcast, i ->
                podcast.liveVideo?.let { live ->
                    val liveHeader = LiveHeader(podcast, live.title, live.youtubeID)
                    val bundle = bundleOf("live_object" to liveHeader)
                    findNavController().navigate(
                        R.id.action_navigation_home_to_liveFragment,
                        bundle
                    )
                }
            }
            slideInBottom()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            homeViewModel.searchPodcastAndEpisodes(it)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
       return false
    }
}