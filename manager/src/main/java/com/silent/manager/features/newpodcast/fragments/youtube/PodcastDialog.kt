package com.silent.manager.features.newpodcast.fragments.youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.silent.core.podcast.Podcast
import com.silent.manager.R
import com.silent.manager.features.newpodcast.NewPodcastViewModel
import kotlinx.android.synthetic.main.podcast_dialog.*

class PodcastDialog : BottomSheetDialogFragment() {

    lateinit var podcast: Podcast
    lateinit var onContinue: () -> Unit
    val newPodcastViewModel: NewPodcastViewModel by lazy {
        ViewModelProvider(requireActivity())[NewPodcastViewModel::class.java]
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
        continue_button.setOnClickListener {
            onContinue.invoke()
            dialog?.dismiss()
        }
        setupPodcast()
    }

    private fun setupPodcast() {
        Glide.with(requireContext()).load(podcast.iconURL).into(podcast_icon)
        podcast_name.text = podcast.name
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