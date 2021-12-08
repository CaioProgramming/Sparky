package com.silent.sparky.features.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilustris.animations.fadeIn
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts
import com.silent.core.utils.WebUtils
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.RC_SIGN_IN
import com.silent.ilustriscore.core.utilities.gone
import com.silent.navigation.ModuleNavigator
import com.silent.navigation.NavigationUtils
import com.silent.sparky.R
import com.silent.sparky.features.home.adapter.ProgramsAdapter
import com.silent.sparky.features.home.adapter.VideoHeaderAdapter
import com.silent.sparky.features.home.data.LiveHeader
import com.silent.sparky.features.home.viewmodel.HomeState
import com.silent.sparky.features.home.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : Fragment() {

    private val homeViewModel = HomeViewModel()
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
        return inflater.inflate(R.layout.home_fragment, container, false)
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


    private fun setupView() {
        podcasts_resume_recycler.adapter = videoHeaderAdapter
        (requireActivity() as AppCompatActivity?)?.run {
            setSupportActionBar(home_toolbar)
            supportActionBar?.title = ""
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
        homeViewModel.homeState.observe(this, {
            when (it) {
                is HomeState.HomeChannelRetrieved -> {
                    videoHeaderAdapter?.updateSection(it.podcastHeader)
                }
                HomeState.HomeError -> {
                    homeViewModel.getAllData()
                }
                HomeState.HomeLiveError -> {
                }
                is HomeState.HomeLivesRetrieved -> {
                    if (it.podcasts.isEmpty()) {
                        lives_recycler_view.gone()
                    } else {
                        lives_recycler_view.fadeIn()
                        lives_recycler_view.adapter =
                            ProgramsAdapter(extractPodcasts(it.podcasts), true) { podcast, index ->
                                val bundle = bundleOf("live_object" to it.podcasts[index])
                                findNavController().navigate(
                                    R.id.action_navigation_home_to_liveFragment,
                                    bundle
                                )
                            }
                    }
                    //view?.showSnackBar("${it.podcasts.size} lives no momento")
                }
                HomeState.InvalidManager -> {
                    setMenuVisibility(false)
                }
                HomeState.ValidManager -> {
                    setMenuVisibility(true)
                    home_animation.setOnClickListener {
                        goToManager()
                    }
                }
            }
        })
        homeViewModel.viewModelState.observe(this, {
            when (it) {
                ViewModelBaseState.RequireAuth -> {

                }
                is ViewModelBaseState.DataListRetrievedState -> {
                    podcasts_resume_recycler.adapter =
                        ProgramsAdapter((it.dataList as podcasts).sortedByDescending { p -> p.subscribe }) { podcast, index ->
                            openPodcast(podcast.id)
                        }
                    podcasts_resume_recycler.layoutManager =
                        GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
                }
                is ViewModelBaseState.ErrorState -> {
                    error_view.showError()
                }
            }
        })
    }

    private fun extractPodcasts(liveHeader: ArrayList<LiveHeader>): ArrayList<Podcast> {
        val podcasts = ArrayList<Podcast>()
        liveHeader.forEach {
            podcasts.add(it.podcast)
        }
        return podcasts
    }

}