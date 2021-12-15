package com.silent.manager.features.newpodcast.fragments

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ilustris.animations.fadeIn
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.silent.manager.R
import com.silent.manager.features.newpodcast.NewPodcastViewModel
import kotlinx.android.synthetic.main.fragment_create_complete.*

class CompleteFragment : Fragment() {

    val newPodcastViewModel: NewPodcastViewModel by lazy {
        ViewModelProvider(requireActivity())[NewPodcastViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        newPodcastViewModel.saveData(newPodcastViewModel.podcast)
    }

    private fun observeViewModel() {
        newPodcastViewModel.viewModelState.observe(this, {
            when (it) {
                ViewModelBaseState.RequireAuth -> TODO()
                ViewModelBaseState.DataDeletedState -> TODO()
                is ViewModelBaseState.DataRetrievedState -> TODO()
                is ViewModelBaseState.DataListRetrievedState -> TODO()
                is ViewModelBaseState.DataSavedState -> {
                    sucess_message.fadeIn()
                    animation.playAnimation()
                    animation.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            sucess_message.text = "Programa salvo com sucesso!"
                            delayedFunction(2000) {
                                requireActivity().finish()
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                    })

                }
                is ViewModelBaseState.DataUpdateState -> TODO()
                is ViewModelBaseState.FileUploadedState -> TODO()
                is ViewModelBaseState.ErrorState -> {
                    sucess_message.fadeIn()
                    sucess_message.text = "Ocorreu um erro inesperado ao salvar :("
                }
            }
        })
    }
}