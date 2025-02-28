package com.silent.manager.features.podcast

import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.component.GroupType
import com.silent.core.component.HostGroup
import com.silent.core.component.HostGroupAdapter
import com.silent.core.instagram.InstagramUserResponse
import com.silent.core.podcast.Host
import com.silent.core.podcast.NEW_HOST
import com.silent.core.podcast.Podcast
import com.silent.core.utils.ImageUtils
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.manager.R
import com.silent.manager.databinding.FragmentManagePodcastBinding
import com.silent.manager.features.manager.PodcastsManagerFragmentArgs
import com.silent.manager.features.newpodcast.fragments.highlight.HIGHLIGHT_TAG
import com.silent.manager.features.newpodcast.fragments.highlight.HighlightColorFragment
import com.silent.manager.features.newpodcast.fragments.highlight.NOTIFICATION_ICON_TAG
import com.silent.manager.features.newpodcast.fragments.highlight.NotificationIconFragment
import com.silent.manager.features.newpodcast.fragments.hosts.HostDialog
import com.silent.manager.features.newpodcast.fragments.hosts.HostInstagramDialog
import com.silent.manager.features.podcast.adapter.VideoHeaderAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PodcastEditingFragment : Fragment() {

    private val args by navArgs<PodcastsManagerFragmentArgs>()
    private val podcastViewModel by viewModel<PodcastManagerViewModel>()
    private var podcastFragmentBinding: FragmentManagePodcastBinding? = null

    lateinit var podcast: Podcast

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
                podcast.slogan = podcastSlogan.text.toString()
                podcast.name = podcastEditText.text.toString()
                podcast.hosts = ArrayList(podcast.hosts.filter { it.name != NEW_HOST })
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Gostaria de atualizar também os episódios e cortes?")
                    .setNegativeButton("Não") { dialog, which ->
                        podcastViewModel.updatePodcastData(podcast)
                    }
                    .setPositiveButton("Sim") { dialog, which ->
                        podcastViewModel.updatePodcastData(
                            podcast,
                            updateClips = true
                        )
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
            podcastNotificationIcon.setOnClickListener {
                NotificationIconFragment.getInstance(Color.parseColor(podcast.highLightColor)) {
                    podcast.notificationIcon = it
                    setupPodcast(podcast)
                }.show(parentFragmentManager, NOTIFICATION_ICON_TAG)
            }
            liveTime.setOnClickListener {
                val calendar = GregorianCalendar.getInstance()
                TimePickerDialog(requireContext(), { view, hour, minute ->
                    podcast.liveTime = hour
                    liveTime.text = "Horário das lives ${hour}h"
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

            }

        }
        setupPodcast(args.podcast)
        observeViewModel()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
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
                    podcastFragmentBinding?.programIcon?.let { imageView ->
                        requireView().showSnackBar(
                            "Podcast Atualizado com sucesso!",
                            snackColor
                        )
                    }

                }
                is ViewModelBaseState.DataRetrievedState -> {
                    setupPodcast(it.data as Podcast)
                }
                else -> {

                }
            }
        }
        podcastViewModel.podcastManagerState.observe(viewLifecycleOwner) {
            when (it) {
                is PodcastManagerViewModel.PodcastManagerState.CutsUpdated -> {
                    requireView().showSnackBar("${it.count} cortes atualizados")
                    podcastViewModel.getSingleData(args.podcast.id)
                }
                is PodcastManagerViewModel.PodcastManagerState.EpisodesUpdated -> {
                    requireView().showSnackBar("${it.count} episódios atualizados")
                    podcastViewModel.getSingleData(args.podcast.id)
                }
                PodcastManagerViewModel.PodcastManagerState.VideoDeleted -> {
                    podcastViewModel.getSingleData(args.podcast.id)
                }
                PodcastManagerViewModel.PodcastManagerState.PodcastUpdateRequest -> {
                    podcastFragmentBinding?.loading?.fadeIn()

                }
                is PodcastManagerViewModel.PodcastManagerState.CutsAndUploadsRetrieved -> {
                    podcastFragmentBinding?.videosRecyclerview?.adapter =
                        VideoHeaderAdapter(it.sections, headerSelected = { header ->
                            MaterialAlertDialogBuilder(requireContext()).setTitle("Atenção")
                                .setMessage("Deseja remover essa playlist ${header.title}?")
                                .setPositiveButton("Confirmar") { d, _ ->
                                    podcastViewModel.deletePlaylist(
                                        header.videos,
                                        header.title ?: ""
                                    )
                                    d.dismiss()
                                }.setNegativeButton("Cancelar") { d, _ ->
                                    d.dismiss()
                                }.show()
                        }, selectVideo = { video, isCuts ->
                            MaterialAlertDialogBuilder(requireContext()).setTitle("Atenção")
                                .setMessage("Deseja remover esse vídeo  ${video.title}?")
                                .setPositiveButton("Confirmar") { d, _ ->
                                    podcastViewModel.deleteVideo(video.id, isCuts)
                                    d.dismiss()
                                }.setNegativeButton("Cancelar") { d, _ ->
                                    d.dismiss()
                                }.show()
                        })
                }
                is PodcastManagerViewModel.PodcastManagerState.PlayslitDeleted -> {
                    requireView().showSnackBar(it.message)
                    podcastViewModel.getSingleData(args.podcast.id)
                }
            }
        }
    }

    private fun setupPodcast(argPodcast: Podcast) {
        podcast = argPodcast
        podcastFragmentBinding?.run {
            podcastEditText.setText(podcast.name)
            Glide.with(requireContext()).load(podcast.cover)
                .error(R.drawable.ic_iconmonstr_connection_1).into(podcastCover)
            Glide.with(requireContext()).load(podcast.iconURL)
                .error(R.drawable.ic_iconmonstr_connection_1).into(programIcon)
            loading.setIndicatorColor(Color.parseColor(podcast.highLightColor))
            highlightColor.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(podcast.highLightColor))
            podcastNotificationIcon.setImageDrawable(
                requireContext().getDrawable(
                    ImageUtils.getNotificationIcon(
                        podcast.notificationIcon
                    ).drawable
                )
            )
            podcastNotificationIcon.circleBackgroundColor = Color.parseColor(podcast.highLightColor)
            podcastSlogan.setText(podcast.slogan)
            liveTime.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(podcast.highLightColor))
            liveTime.text = "Horário das lives ${argPodcast.liveTime}h"
        }
        updateHosts()
        podcastViewModel.getVideosAndCuts(argPodcast.id)
    }

    private fun selectHost(host: Host, type: GroupType) {
        if (host.name == NEW_HOST) {
            HostInstagramDialog.getInstance(type) {
                confirmUser(it, type)
            }.show(childFragmentManager, "INSTAGRAMDIALOG")
        } else {
            when (type) {
                GroupType.HOSTS -> podcast.hosts.remove(host)
            }
            updateHosts()
        }

    }


    private fun updateHosts() {
        podcastFragmentBinding?.run {
            programIcon.invalidate()
            podcastFragmentBinding?.hostsRecyclerview?.adapter = HostGroupAdapter(
                listOf(
                    HostGroup("Hosts", podcast.hosts)
                ),
                true, ::selectHost,
                podcast.highLightColor
            )
        }

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
            }
            updateHosts()
        }.show(childFragmentManager, "CONFIRMHOST")
    }


}