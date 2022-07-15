package com.silent.sparky.features.profile.settings

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
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.podcast.NEW_PODCAST
import com.silent.core.podcast.Podcast
import com.silent.core.users.User
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentSettingsBinding
import com.silent.sparky.features.profile.dialog.PreferencesDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val args by navArgs<SettingsFragmentArgs>()
    var settingsBinding: FragmentSettingsBinding? = null
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsBinding = FragmentSettingsBinding.inflate(inflater)
        return settingsBinding?.root
    }

    override fun onResume() {
        super.onResume()
        setupUser(args.userObject)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUser(args.userObject)
        observeViewModel()
    }

    private fun observeViewModel() {
        settingsViewModel.settingsState.observe(viewLifecycleOwner) {
            when (it) {
                is SettingsViewModel.SettingsState.PodcastsPreferencesRetrieve -> setupPodcasts(it.podcasts)
            }
        }
        settingsViewModel.viewModelState.observe(viewLifecycleOwner) {
            when (it) {
                is ViewModelBaseState.ErrorState -> requireView().showSnackBar(
                    it.dataException.code.message,
                    backColor = Color.RED
                )
            }
        }
    }

    private fun setupPodcasts(podcasts: ArrayList<Podcast>) {
        settingsBinding?.preferedPodcastsRecycler?.adapter = PodcastListAdapter(podcasts.apply {
            add(Podcast.newPodcast)
        }) { podcast, i ->
            if (podcast.id == NEW_PODCAST) {
                PreferencesDialogFragment.buildDialog(childFragmentManager) {
                    settingsViewModel.loadSettings()
                }
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Tem certeza")
                    .setMessage("Essa ação irá remover o podcast ${podcast.name} de seus favoritos")
                    .setNegativeButton("Remover") { dialog, which ->
                        settingsViewModel.removeFavorite(podcast.id)
                    }
                    .setPositiveButton("Cancelar") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun setupUser(user: User) {
        settingsBinding?.run {
            username.text = user.name
            flowname.text = user.flowUserName
            Glide.with(requireContext()).load(user.profilePic)
                .placeholder(R.drawable.ic_iconmonstr_flower).into(userPhoto)
            flowAccountButton.setOnClickListener {
                findNavController().navigate(R.id.action_settingsFragment_to_flowLinkDialog)
            }
            settingsViewModel.loadSettings()
        }
    }

}