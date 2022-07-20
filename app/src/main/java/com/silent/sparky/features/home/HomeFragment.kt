package com.silent.sparky.features.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.animations.slideInBottom
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
import com.silent.core.podcast.PodcastHeader
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
        homeFragmentBinding?.showLoading()
        setupView()
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        homeViewModel.getHome()
    }

    private fun openPodcast(id: String) {
        val bundle = bundleOf("podcast_id" to id)
        findNavController().navigate(R.id.action_navigation_home_to_podcastFragment, bundle)
    }

    private fun openChannel(url: String) {
        WebUtils(requireContext()).openYoutubeChannel(url)
    }

    private fun setupView() {
        homeFragmentBinding?.run {
            (requireActivity() as AppCompatActivity?)?.run {
                setSupportActionBar(homeToolbar)
                supportActionBar?.title = ""
            }
            homeSearch.setQuery("", false)
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
                    homeFragmentBinding?.showError("Ocorreu um erro inesperado ao carregar.") {
                        homeViewModel.getAllData()
                    }
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
                HomeState.LoadingSearch -> {
                    homeFragmentBinding?.run {
                        showLoading()
                    }
                }
                is HomeState.HomeSearchRetrieved -> {
                    if (it.podcastHeader.isNotEmpty()) {
                        setupHome(it.podcastHeader)
                    } else {
                        homeFragmentBinding?.showError("Nenhum resultado encontrado para sua busca.") {
                            homeFragmentBinding?.homeSearch?.setQuery("",false)
                            homeViewModel.getHome()
                        }
                    }
                }
                else -> {}
            }
        }
        homeViewModel.viewModelState.observe(viewLifecycleOwner) {
            when (it) {
                ViewModelBaseState.LoadingState -> {
                    homeFragmentBinding?.showLoading()
                }

                ViewModelBaseState.LoadCompleteState -> {
                    homeFragmentBinding?.stopLoading()
                }

                ViewModelBaseState.RequireAuth -> {
                    homeFragmentBinding?.showError("Você precisa estar logado para utilizar o app.") {
                        mainActViewModel.updateState(MainActViewModel.MainActState.RequireLoginState)
                    }
                }
                is ViewModelBaseState.DataListRetrievedState -> {
                    homeFragmentBinding?.podcastsResumeRecycler?.run {
                        adapter = PodcastsLiveAdapter((it.dataList as podcasts).sortedByDescending { p -> p.subscribe }) { podcast, index ->
                             openPodcast(podcast.id)
                            }
                        layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
                        homeFragmentBinding?.stopLoading()
                    }
                }
                is ViewModelBaseState.ErrorState -> {
                    if (it.dataException.code == ErrorType.AUTH) {
                        homeFragmentBinding?.showError("Você precisa estar logado para utilizar o app.") {
                            mainActViewModel.updateState(MainActViewModel.MainActState.RequireLoginState)
                        }
                    } else {
                        homeFragmentBinding?.showError("Ocorreu um erro inesperado(${it.dataException.code.message}") {
                            homeViewModel.getHome()
                        }
                    }
                }
                else -> {}
            }
        }
        mainActViewModel.actState.observe(viewLifecycleOwner) {
            if (it is MainActViewModel.MainActState.NavigateToPodcast) {
                openPodcast(it.podcastId)
                mainActViewModel.notificationOpen()
            }
        }
    }

    private fun HomeFragmentBinding.showLoading() {
        homeAnimation.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0.5f }
        homeToolbar.fadeOut()
        mainContent.fadeOut()
    }

    private fun HomeFragmentBinding.stopLoading() {
        homeAnimation.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0.0f }
        homeTitle.fadeIn()
        homeToolbar.fadeIn()
        mainContent.slideInBottom()
    }

    private fun HomeFragmentBinding.showError(message: String, tryAgainClick: () -> Unit) {

        errorView.run {
            errorAnimation.playAnimation()
             errorMessage.text = message
             errorButton.setOnClickListener {
                tryAgainClick.invoke()
                 root.fadeOut()
            }
            root.fadeIn()
        }

    }

    private fun setupHome(headers: ArrayList<PodcastHeader>) {
        homeFragmentBinding?.run {
            podcastsResumeRecycler.adapter = VideoHeaderAdapter(headers, headerSelected = { header ->
                    header.playlistId?.let { id -> openPodcast(id) }
                }, ::openChannel, selectPodcast = {
                    openPodcast(it.id)
                })
            podcastsResumeRecycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            stopLoading()
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