package com.silent.manager.features.newpodcast.fragments.hosts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.silent.core.component.HostAdapter
import com.silent.core.instagram.InstagramUserResponse
import com.silent.core.podcast.Host
import com.silent.core.podcast.NEW_HOST
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.manager.R
import com.silent.manager.features.newpodcast.NewPodcastViewModel
import com.silent.manager.states.HostState
import kotlinx.android.synthetic.main.fragment_hosts_data.*

class HostsFormFragment : Fragment() {

    private val newPodcastViewModel: NewPodcastViewModel by lazy {
        ViewModelProvider(requireActivity())[NewPodcastViewModel::class.java]
    }

    private val hostAdapter = HostAdapter(arrayListOf(Host(NEW_HOST, "", "")), ::selectHost)

    private fun selectHost(host: Host) {
        if (host.name == NEW_HOST) {
            HostInstagramDialog.getInstance(this::confirmUser)
                .show(childFragmentManager, "INSTAGRAMDIALOG")
        } else {
            newPodcastViewModel.deleteHost(host)
        }
    }

    private fun confirmUser(instagramUser: InstagramUserResponse) {
        val host = Host(
            instagramUser.full_name,
            instagramUser.profile_pic_url,
            instagramUser.username
        )
        HostDialog.getInstance(host) {
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
        instagram_hosts_recyclerview.adapter = hostAdapter
        host_next_button.setOnClickListener {
            findNavController().navigate(R.id.action_podcastGetHostsFragment_to_completeFragment)
        }
    }

    private fun observeViewModel() {
        newPodcastViewModel.hostState.observe(this, {
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
        })
    }

}