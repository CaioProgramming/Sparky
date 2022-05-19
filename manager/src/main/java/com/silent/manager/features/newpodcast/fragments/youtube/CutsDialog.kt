package com.silent.manager.features.newpodcast.fragments.youtube

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.silent.core.podcast.Podcast
import com.silent.manager.R
import com.silent.manager.databinding.CutsDialogBinding
import com.silent.manager.features.newpodcast.NewPodcastViewModel
import com.silent.manager.states.NewPodcastState

class CutsDialog : BottomSheetDialogFragment() {

    private val podcastAdapter = PodcastHeaderAdapter(arrayListOf(), Color.BLACK) {
        onSelectCut(it)
        dialog?.dismiss()
    }
    private val newPodcastViewModel: NewPodcastViewModel by lazy {
        ViewModelProvider(requireActivity())[NewPodcastViewModel::class.java]
    }
    lateinit var onSelectCut: (Podcast) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cuts_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        CutsDialogBinding.bind(view).flowCutsChannels.adapter = podcastAdapter
        newPodcastViewModel.getRelatedCuts()
    }

    private fun observeViewModel() {
        newPodcastViewModel.newPodcastState.observe(viewLifecycleOwner) {
            if (it is NewPodcastState.RelatedPodcastsRetrieved) {
                podcastAdapter.updateHeaders(ArrayList(it.podcastsHeader))
            }
        }
    }

    companion object {
        fun getInstance(cutSelected: (Podcast) -> Unit): CutsDialog {
            return CutsDialog().apply {
                onSelectCut = cutSelected
            }
        }
    }
}