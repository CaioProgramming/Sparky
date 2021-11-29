package com.silent.sparky.manager.features.manager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.silent.core.podcast.Podcast
import com.silent.core.podcast.podcasts
import com.silent.ilustriscore.core.model.ViewModelBaseActions
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.LoginHelper
import com.silent.ilustriscore.core.utilities.getView
import com.silent.ilustriscore.core.utilities.showSnackBar
import com.silent.sparky.manager.R
import com.silent.sparky.manager.features.NewPodcastActivity
import com.silent.sparky.manager.features.manager.ui.adapter.PodcastManagerAdapter
import com.silent.sparky.manager.features.manager.viewmodel.ManagerViewModel
import kotlinx.android.synthetic.main.activity_manager.*

class ManagerActivity : AppCompatActivity() {
    val viewModel = ManagerViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        setupView()
        observeViewModel()
        viewModel.dispatchViewAction(ViewModelBaseActions.GetAllDataAction)
    }

    private fun setupView() {
        new_podcast_button.setOnClickListener {
            NewPodcastActivity.launchIntent(this)
        }
    }

    private fun showSnackBar(message: String) {
        getView().showSnackBar(message)
    }

    private fun observeViewModel() {
        viewModel.viewModelState.observe(this, {
            when (it) {
                ViewModelBaseState.RequireAuth -> {
                    val providers = listOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                        AuthUI.IdpConfig.EmailBuilder().build()
                    )
                    LoginHelper.signIn(this, providers, R.style.Theme_Sparky, R.mipmap.ic_launcher)
                }
                ViewModelBaseState.DataDeletedState -> {
                    showSnackBar("Podcast removido com sucesso")
                }
                is ViewModelBaseState.DataListRetrievedState -> {
                    podcasts_recycler.adapter = PodcastManagerAdapter(it.dataList as podcasts) {
                        MaterialAlertDialogBuilder(this)
                            .setTitle("O que deseja fazer?")
                            .setItems(arrayOf("Editar", "Excluir")) { dialog, wich ->
                                dialog.dismiss()
                                when (wich) {
                                    0 -> editPodcast(it)
                                    1 -> deletePodcast(it)
                                }
                            }.show()
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
        })
    }

    private fun deletePodcast(podcast: Podcast) {
        viewModel.dispatchViewAction(ViewModelBaseActions.DeleteDataAction(podcast.id))
    }

    private fun editPodcast(podcast: Podcast) {
        TODO("Not yet implemented")
    }

}