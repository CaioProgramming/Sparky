package com.silent.manager.features.newpodcast.fragments.hosts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.silent.core.component.GroupType
import com.silent.core.component.HostAdapter
import com.silent.core.instagram.InstagramUserResponse
import com.silent.core.podcast.Host
import com.silent.core.podcast.NEW_HOST
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.manager.R
import com.silent.manager.databinding.FragmentHostsDataBinding
import com.silent.manager.features.newpodcast.NewPodcastViewModel
import com.silent.manager.features.newpodcast.fragments.highlight.HIGHLIGHT_TAG
import com.silent.manager.features.newpodcast.fragments.highlight.HighlightColorFragment
import com.silent.manager.states.HostState

class HostsFormFragment : Fragment() {

    private val newPodcastViewModel: NewPodcastViewModel by lazy {
        ViewModelProvider(requireActivity())[NewPodcastViewModel::class.java]
    }

    private val hostAdapter = HostAdapter(arrayListOf(Host(NEW_HOST, "", "")), true, ::selectHost)

    private fun selectHost(host: Host) {
        if (host.name == NEW_HOST) {
            HostInstagramDialog.getInstance(GroupType.HOSTS, this::confirmUser)
                .show(childFragmentManager, "INSTAGRAMDIALOG")
        } else {
            newPodcastViewModel.deleteHost(host)
        }
    }

    private fun confirmUser(instagramUser: InstagramUserResponse) {
        val host = Host(
            instagramUser.full_name,
            instagramUser.profile_pic_url
        )
        HostDialog.getInstance(GroupType.HOSTS, host) {
            newPodcastViewModel.updateHosts(host)
        }.show(childFragmentManager, "CONFIRMHOST")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hosts_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        FragmentHostsDataBinding.bind(view).run {
            instagramHostsRecyclerview.adapter = hostAdapter
            hostNextButton.setOnClickListener {
                HighlightColorFragment.getInstance {
                    newPodcastViewModel.podcast.highLightColor = it
                    findNavController().navigate(R.id.action_podcastGetHostsFragment_to_completeFragment)
                }.show(childFragmentManager, HIGHLIGHT_TAG)
            }
        }

    }

    private fun observeViewModel() {
        newPodcastViewModel.hostState.observe(viewLifecycleOwner) {
            when (it) {
                is HostState.HostUpdated -> {
                    hostAdapter.updateHost(it.host)
                }
                HostState.ErrorFetchInstagram -> {
                    view?.showSnackBar(
                        "Ocorreu um erro ao recuperar dados do Instagram",
                        backColor = Color.RED
                    )
                }
                is HostState.HostDeleted -> {
                    hostAdapter.refresh(it.hosts)
                }

                else -> {

                }
            }
        }
    }

}