package com.silent.manager.features.newpodcast.fragments.youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.silent.core.podcast.Podcast
import com.silent.manager.R
import com.silent.manager.databinding.PodcastDialogBinding

class PodcastDialog : BottomSheetDialogFragment() {

    lateinit var podcast: Podcast
    lateinit var onContinue: () -> Unit
    private val podcastDialogBinding by lazy {
        view?.let { PodcastDialogBinding.bind(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.podcast_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        podcastDialogBinding?.run {
            continueButton.setOnClickListener {
                onContinue.invoke()
                dialog?.dismiss()
            }
        }

        setupPodcast()
    }

    private fun setupPodcast() {
        podcastDialogBinding?.podcastIcon?.let {
            Glide.with(requireContext()).load(podcast.iconURL).into(
                it
            )
        }
        podcastDialogBinding?.podcastName?.text = podcast.name
    }

    companion object {
        fun newInstance(pdcast: Podcast, continueClick: () -> Unit): PodcastDialog {
            return PodcastDialog().apply {
                podcast = pdcast
                onContinue = continueClick
            }
        }
    }

}