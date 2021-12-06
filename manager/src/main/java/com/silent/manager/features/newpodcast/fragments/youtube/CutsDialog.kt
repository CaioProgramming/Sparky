package com.silent.manager.features.newpodcast.fragments.youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.silent.core.podcast.Podcast
import com.silent.manager.R
import com.silent.manager.features.newpodcast.NewPodcastViewModel
import com.silent.manager.states.NewPodcastState
import kotlinx.android.synthetic.main.cuts_dialog.*

class CutsDialog : BottomSheetDialogFragment() {

    private val podcastAdapter = PodcastAdapter(arrayListOf()) {
        onSelectCut.invoke(it)
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
        flow_cuts_channels.adapter = podcastAdapter
        newPodcastViewModel.getRelatedCuts()
    }

    private fun observeViewModel() {
        newPodcastViewModel.newPodcastState.observe(this, {
            if (it is NewPodcastState.RelatedCutsRetrieved) {
                podcastAdapter.updateAdapter(it.podcast)
            }
        })
    }

    companion object {
        fun getInstance(cutSelected: (Podcast) -> Unit): CutsDialog {
            return CutsDialog().apply {
                onSelectCut = cutSelected
            }
        }
    }
}