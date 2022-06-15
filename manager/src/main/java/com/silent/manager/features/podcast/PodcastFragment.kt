package com.silent.manager.features.podcast

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.silent.core.component.GroupType
import com.silent.core.component.HostGroup
import com.silent.core.component.HostGroupAdapter
import com.silent.core.instagram.InstagramUserResponse
import com.silent.core.podcast.Host
import com.silent.core.podcast.NEW_HOST
import com.silent.core.podcast.Podcast
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.manager.R
import com.silent.manager.databinding.FragmentManagePodcastBinding
import com.silent.manager.features.manager.PodcastsManagerFragmentArgs
import com.silent.manager.features.newpodcast.fragments.highlight.HIGHLIGHT_TAG
import com.silent.manager.features.newpodcast.fragments.highlight.HighlightColorFragment
import com.silent.manager.features.newpodcast.fragments.hosts.HostDialog
import com.silent.manager.features.newpodcast.fragments.hosts.HostInstagramDialog

class PodcastFragment : Fragment() {

    private val args by navArgs<PodcastsManagerFragmentArgs>()
    private val podcastViewModel by lazy { PodcastViewModel(requireActivity().application) }
    private var podcastFragmentBinding: FragmentManagePodcastBinding? = null

    lateinit var podcast: Podcast


    private fun getArgPodcast() = args.podcast

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        podcastFragmentBinding = FragmentManagePodcastBinding.inflate(inflater)
        return podcastFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        podcastFragmentBinding?.run {
            updatePodcast.setOnClickListener {
                podcast.name = podcastEditText.text.toString()
                podcast.hosts = ArrayList(podcast.hosts.filter { it.name != NEW_HOST })
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Gostaria de atualizar também os episódios e cortes?")
                    .setNegativeButton("Não") { dialog, which ->
                        podcastViewModel.updatePodcastData(podcast)
                    }
                    .setPositiveButton("Sim") { dialog, which ->
                        podcastViewModel.updatePodcastData(podcast, true)
                    }.show()
            }
            removePodcast.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Tem certeza")
                    .setMessage("Você está prestes a remover ${podcast.name} essa ação não pode ser desfeita.")
                    .setNegativeButton("Remover") { dialog, which ->
                        podcastViewModel.deleteData(podcast.id)
                    }
                    .setPositiveButton("Cancelar") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
            highlightColor.setOnClickListener {
                HighlightColorFragment.getInstance {
                    podcast.highLightColor = it
                    setupPodcast(podcast)
                }.show(parentFragmentManager, HIGHLIGHT_TAG)
            }
        }
        setupPodcast(args.podcast)
        observeViewModel()
    }

    private fun observeViewModel() {
        podcastViewModel.viewModelState.observe(viewLifecycleOwner) {
            when (it) {
                ViewModelBaseState.DataDeletedState -> {
                    findNavController().popBackStack()
                }
                is ViewModelBaseState.DataUpdateState -> {
                    podcastFragmentBinding?.loading?.fadeOut()
                    val snackColor = Color.parseColor(podcast.highLightColor)
                    view?.showSnackBar(
                        "Podcast Atualizado com sucesso!",
                        snackColor,
                        actionText = "Ok",
                        action = {
                            findNavController().popBackStack()
                        })
                }
                else -> {

                }
            }
        }
        podcastViewModel.podcastManagerState.observe(viewLifecycleOwner) {
            when(it) {
                is PodcastViewModel.PodcastManagerState.CutsUpdated -> {
                    requireView().showSnackBar("${it.count} cortes atualizados")
                }
                is PodcastViewModel.PodcastManagerState.EpisodesUpdated -> {
                    requireView().showSnackBar("${it.count} episódios atualizados")

                }
                PodcastViewModel.PodcastManagerState.PodcastUpdateRequest -> {
                    podcastFragmentBinding?.loading?.fadeIn()
                }
            }
        }
    }

    private fun setupPodcast(argPodcast: Podcast) {
        podcast = argPodcast
        podcastFragmentBinding?.run {
            podcastEditText.setText(podcast.name)
            Glide.with(requireContext()).load(podcast.cover).error(R.drawable.ic_iconmonstr_connection_1).into(podcastCover)
            Glide.with(requireContext()).load(podcast.iconURL).error(R.drawable.ic_iconmonstr_connection_1).into(programIcon)
            loading.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(podcast.highLightColor))
        }
        updateHosts()
    }

    private fun selectHost(host: Host, type: GroupType) {
        if (host.name == NEW_HOST) {
            HostInstagramDialog.getInstance(type) {
                confirmUser(it, type)
            }.show(childFragmentManager, "INSTAGRAMDIALOG")
        } else {
            when (type) {
                GroupType.HOSTS -> podcast.hosts.remove(host)
                GroupType.GUESTS -> podcast.weeklyGuests.remove(host)
            }
            updateHosts()
        }

    }


    private fun updateHosts() {
        podcastFragmentBinding?.hostsRecyclerview?.adapter = HostGroupAdapter(
            listOf(
                HostGroup("Hosts", podcast.hosts),
                HostGroup("Convidados da semana", podcast.weeklyGuests, GroupType.GUESTS)
            ),
            true, ::selectHost,
            podcast.highLightColor
        )
    }

    private fun confirmUser(instagramUser: InstagramUserResponse, type: GroupType) {
        val host = Host(
            instagramUser.full_name,
            instagramUser.profile_pic_url,
            instagramUser.username
        )
        HostDialog.getInstance(type, host) {
            when (type) {
                GroupType.HOSTS -> podcast.hosts.add(host)
                GroupType.GUESTS -> podcast.weeklyGuests.add(host)
            }
            updateHosts()
        }.show(childFragmentManager, "CONFIRMHOST")
    }


}