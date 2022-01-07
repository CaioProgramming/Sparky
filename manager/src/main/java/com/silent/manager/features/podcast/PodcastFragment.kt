package com.silent.manager.features.podcast

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.silent.core.component.HostAdapter
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
    private val podcastViewModel = PodcastViewModel()
    private val podcastFragmentBinding by lazy {
        view?.let { FragmentManagePodcastBinding.bind(it) }
    }

    lateinit var podcast: Podcast


    private fun getArgPodcast() = args.podcast

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manage_podcast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        podcastFragmentBinding?.run {
            podcastEditText.addTextChangedListener {
                editPodcastButton.isEnabled =
                    (podcastEditText.text.toString() != getArgPodcast().name)
            }
            editPodcastButton.setOnClickListener {
                podcast.name = podcastEditText.text.toString()
                podcast.hosts = ArrayList(podcast.hosts.filter { it.name != NEW_HOST })
                podcastViewModel.editData(podcast)
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
        podcastViewModel.viewModelState.observe(this, {
            when (it) {
                ViewModelBaseState.DataDeletedState -> {
                    findNavController().popBackStack()
                }
                is ViewModelBaseState.DataUpdateState -> {
                    view?.showSnackBar(
                        "Podcast Atualizado com sucesso!",
                        Color.GREEN,
                        actionText = "Ok",
                        action = {
                            findNavController().popBackStack()
                        })
                }
                else -> {

                }
            }
        })
    }

    private fun setupPodcast(argPodcast: Podcast) {
        podcast = argPodcast
        podcastFragmentBinding?.podcastEditText?.setText(podcast.name)
        podcastFragmentBinding?.programIcon?.let {
            Glide.with(requireContext()).load(podcast.iconURL).into(it)
            if (podcast.highLightColor.isNotEmpty()) {
                it.borderColor = Color.parseColor(podcast.highLightColor)
                podcastFragmentBinding?.highlightColor?.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(podcast.highLightColor))
            }
        }
        updateHosts()
    }

    private fun selectHost(host: Host) {
        if (host.name == NEW_HOST) {
            HostInstagramDialog.getInstance(this::confirmUser)
                .show(childFragmentManager, "INSTAGRAMDIALOG")
        } else {
            podcast.hosts.remove(host)
        }
        updateHosts()
    }

    private fun updateHosts() {
        val hosts = ArrayList<Host>()
        hosts.addAll(podcast.hosts)
        podcastFragmentBinding?.hostsRecyclerview?.adapter = HostAdapter(hosts, true, ::selectHost)
        podcastFragmentBinding?.editPodcastButton?.isEnabled = true
    }

    private fun confirmUser(instagramUser: InstagramUserResponse) {
        val host = Host(
            instagramUser.full_name,
            instagramUser.profile_pic_url,
            instagramUser.username
        )
        HostDialog.getInstance(host) {
            podcast.hosts.add(host)
            updateHosts()
        }.show(childFragmentManager, "CONFIRMHOST")
    }


}