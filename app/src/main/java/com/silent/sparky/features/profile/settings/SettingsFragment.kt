package com.silent.sparky.features.profile.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.podcast.NEW_PODCAST
import com.silent.core.podcast.Podcast
import com.silent.core.users.User
import com.silent.core.utils.WebUtils
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.sparky.R
import com.silent.sparky.databinding.FragmentSettingsBinding
import com.silent.sparky.features.profile.dialog.FlowLinkDialog
import com.silent.sparky.features.profile.dialog.PREF_TAG
import com.silent.sparky.features.profile.dialog.PreferencesDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

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
                SettingsViewModel.SettingsState.UserSignedOut -> {
                    requireActivity().finishAffinity()
                }
            }
        }
        settingsViewModel.viewModelState.observe(viewLifecycleOwner) {
            when (it) {
                is ViewModelBaseState.ErrorState -> requireView().showSnackBar(
                    it.dataException.code.message,
                    backColor = Color.RED
                )
                is ViewModelBaseState.DataDeletedState -> {
                    requireView().showSnackBar("Conta deletada.", backColor = Color.BLACK)
                    delayedFunction(5000) {
                        requireActivity().finishAffinity()
                    }
                }
                else -> {}
            }
        }
    }

    private fun setupPodcasts(podcasts: ArrayList<Podcast>) {
        settingsBinding?.preferedPodcastsRecycler?.adapter = PodcastListAdapter(podcasts.apply {
            add(Podcast.newPodcast)
        }) { podcast, i ->
            if (podcast.id == NEW_PODCAST) {
                PreferencesDialogFragment.buildDialog {
                    settingsViewModel.loadSettings()
                }.show(childFragmentManager, PREF_TAG)
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Tem certeza?")
                    .setMessage("Quer mesmo remover ${podcast.name} de seus favoritos?")
                    .setNegativeButton("Remover") { dialog, which ->
                        settingsViewModel.removeFavorite(podcast.id)
                    }.setPositiveButton("Cancelar") { dialog, which ->
                        dialog.dismiss()
                    }.show()
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
                FlowLinkDialog(user).show(
                    requireActivity().supportFragmentManager,
                    "FLOWLINKDIALOG"
                )
            }
            signOutButton.setOnClickListener {
                settingsViewModel.logout()
            }

            deleteAccountButton.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext()).setTitle("Tem certeza?")
                    .setMessage("Essa ação irá excluir sua conta e não pode ser revertida.")
                    .setPositiveButton("Confirmar") { dialog, _ ->
                        settingsViewModel.deleteData(user.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }

            nv99Button.setOnClickListener {
                WebUtils(requireContext()).openNv99()
            }
            val date = GregorianCalendar.getInstance()
            appInfo.text =
                "Desenvolvido por Silent systems\n2021 a ${date.get(GregorianCalendar.YEAR)}"
            settingsViewModel.loadSettings()
        }
    }

}