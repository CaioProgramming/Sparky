package com.silent.manager.features.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.ilustris.ui.extensions.showSnackBar
import com.silent.core.podcast.podcasts
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.manager.R
import com.silent.manager.databinding.FragmentManagerBinding
import com.silent.manager.features.manager.adapter.PodcastManagerAdapter
import com.silent.manager.features.manager.update.PodcastUpdateDialog
import com.silent.manager.features.newpodcast.NewPodcastActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PodcastsManagerFragment : Fragment() {
    private val viewModel by viewModel<ManagerViewModel>()
    private var fragmentManagerBinding: FragmentManagerBinding? = null
    private var updateDialog: PodcastUpdateDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentManagerBinding = FragmentManagerBinding.inflate(inflater)
        return fragmentManagerBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentManagerBinding?.setupView()
    }

    private fun FragmentManagerBinding.setupView() {
        newPodcastButton.setOnClickListener {
            NewPodcastActivity.launchIntent(requireContext())
        }
        updateEpisodes.setOnClickListener {
            viewModel.updatePodcastsEpisodesAndCuts()
        }
        observeViewModel()
        viewModel.getAllData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllData()
    }

    private fun showSnackBar(message: String) {
        view?.showSnackBar(message)
    }

    private fun observeViewModel() {
        viewModel.viewModelState.observe(viewLifecycleOwner) {
            when (it) {
                ViewModelBaseState.RequireAuth -> {
                    activity?.finish()
                }
                ViewModelBaseState.DataDeletedState -> {
                    showSnackBar("Podcast removido com sucesso")
                    viewModel.getAllData()
                }
                is ViewModelBaseState.DataListRetrievedState -> {
                    val podcasts = (it.dataList as podcasts).sortedByDescending { p -> p.subscribe }
                    fragmentManagerBinding?.podcastsRecycler?.adapter =
                        PodcastManagerAdapter(ArrayList(podcasts)) { podcast ->
                            val bundle = bundleOf("podcast" to podcast)

                            findNavController().navigate(
                                R.id.action_podcastsManagerFragment_to_podcastFragment,
                                bundle
                            )
                        }
                }
                is ViewModelBaseState.DataSavedState -> {
                    showSnackBar("Podcast adicionado com sucesso")
                }
                is ViewModelBaseState.DataUpdateState -> {
                    showSnackBar("Podcast atualizado com sucesso")
                }
                is ViewModelBaseState.ErrorState -> {
                    showSnackBar("Ocorreu um erro inesperado")
                }
                else -> {
                    //DO NOTHING
                }
            }
        }
        viewModel.managerState.observe(viewLifecycleOwner) {
            when(it) {
                is ManagerViewModel.ManagerState.PodcastUpdated -> {
                    updateDialog?.updatePodcastStatus(it.index)
                }
                ManagerViewModel.ManagerState.UpdateComplete -> {
                    updateDialog?.dialog?.dismiss()
                }
                is ManagerViewModel.ManagerState.UpdateError -> {
                    //updateDialog?.dialog?.dismiss()
                    showSnackBar("Ocorreu um erro ao atualizar os podcasts")
                }
                is ManagerViewModel.ManagerState.UpdatingPodcasts -> {
                    if (updateDialog == null) {
                        updateDialog = PodcastUpdateDialog(requireContext(), it.podcasts)
                    }
                    updateDialog?.buildDialog()
                }
            }
        }
    }
}