package com.silent.sparky.features.profile.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentPreferencesBinding
import com.silent.sparky.features.profile.adapter.PodcastAdapter
import com.silent.sparky.features.profile.viewmodel.PreferencesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "PREFERENCES_DIALOG"

class PreferencesDialogFragment : BottomSheetDialogFragment() {

    private val preferencesViewModel by viewModel<PreferencesViewModel>()
    private var fragmentPreferencesBinding: FragmentPreferencesBinding? = null
    private var podcastAdapter = PodcastAdapter(ArrayList(), ::selectPodcast)
    private val selectedPodcasts = ArrayList<String>()
    private lateinit var onDismiss: () -> Unit

    private fun selectPodcast(podcast: Podcast) {
        addOrRemovePodcast(podcast)
    }

    fun isEnable() = selectedPodcasts.size >= 3

    fun addOrRemovePodcast(podcast: Podcast) {
        if (!selectedPodcasts.contains(podcast.id)) {
            selectedPodcasts.add(podcast.id)
            podcastAdapter.selectPodcast(podcast.id)
        } else {
            selectedPodcasts.remove(podcast.id)
            podcastAdapter.removePodcast(podcast.id)
        }
        checkSaveButton()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(STYLE_NORMAL, R.style.Theme_Sparky)
        fragmentPreferencesBinding = FragmentPreferencesBinding.inflate(inflater)
        return fragmentPreferencesBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        fragmentPreferencesBinding?.saveButton?.setOnClickListener {
            preferencesViewModel.savePodcastPreferences(selectedPodcasts)
        }
        preferencesViewModel.getAllData()
        checkSaveButton()
    }

    private fun observeViewModel() {
        preferencesViewModel.viewModelState.observe(viewLifecycleOwner) {
            when (it) {
                is ViewModelBaseState.DataListRetrievedState -> {
                    val podcasts = it.dataList as ArrayList<Podcast>
                    setupPodcasts(ArrayList(podcasts.sortedByDescending { p -> p.subscribe }))
                    preferencesViewModel.getPodcastPreferences()
                }
                is ViewModelBaseState.ErrorState -> view?.showSnackBar(
                    it.dataException.code.message,
                    backColor = Color.RED
                )
            }
        }
        preferencesViewModel.preferencesViewState.observe(viewLifecycleOwner) {
            checkSaveButton()
            when (it) {
                is PreferencesViewModel.PreferencesViewState.PodcastAdded -> {
                    podcastAdapter.selectPodcast(it.podcast)
                }
                is PreferencesViewModel.PreferencesViewState.PodcastRemoved -> {
                    podcastAdapter.removePodcast(it.podcast)
                }
                PreferencesViewModel.PreferencesViewState.PreferencesError -> {
                    view?.showSnackBar(
                        "Ocorreu um erro ao salvar suas preferências",
                        backColor = Color.RED
                    )
                }
                PreferencesViewModel.PreferencesViewState.PreferencesSaved -> {
                    onDismiss()
                    dismiss()
                }
                is PreferencesViewModel.PreferencesViewState.PodcastPreferencesRetrieved -> {
                    it.favorites.forEach { id ->
                        podcastAdapter.selectPodcast(id)
                    }
                }
            }
        }
    }

    private fun checkSaveButton() {
        fragmentPreferencesBinding?.run {
            saveButton.isEnabled = isEnable()
            val backColor = if (isEnable()) R.color.material_yellow800 else R.color.material_grey700
            val textColor = if (isEnable()) R.color.material_black else R.color.material_grey400
            saveButton.setBackgroundColor(requireContext().getColor(backColor))
            saveButton.setTextColor(requireContext().getColor(textColor))
        }

    }

    private fun setupPodcasts(podcasts: podcasts) {
        fragmentPreferencesBinding?.run {
            podcastAdapter = PodcastAdapter(podcasts, ::selectPodcast)
            podcastsRecycler.adapter = podcastAdapter
        }
    }


    companion object {
        fun buildDialog(fragmentManager: FragmentManager, onDismiss: () -> Unit) =
            PreferencesDialogFragment().apply {
                this.onDismiss = onDismiss
            }.show(fragmentManager, TAG)
    }

}