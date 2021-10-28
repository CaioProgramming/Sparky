package com.silent.sparky.program

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.silent.core.program.Program
import com.silent.ilustriscore.core.utilities.gone
import com.silent.sparky.R
import com.silent.sparky.program.adapter.VideosAdapter
import kotlinx.android.synthetic.main.program_frame_layout.*

class ProgramItemFragment: Fragment() {

    var program: Program? = null
    private val programViewModel = ProgramViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.program_frame_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        program?.let {
            programViewModel.getChannelData(it)
            see_more_button.setOnClickListener { v ->
                ProgramActivity.getLaunchIntent(it, requireContext())
            }
            Glide.with(requireContext()).load(it.iconURL).into(program_icon)
        }
        programViewModel.channelState.observe(viewLifecycleOwner, {
            when(it) {
                is ProgramViewModel.ChannelState.ChannelDataRetrieved -> {
                    it.channelDetails.snippet.run {
                        group_title.text = title
                        Glide.with(requireContext()).load(program!!.iconURL).into(program_icon)
                    }
                    programViewModel.getChannelVideos(it.channelDetails.contentDetails.relatedPlaylists.uploads)
                }
                is ProgramViewModel.ChannelState.ChannelUploadsRetrieved -> {
                    videos_recycler.adapter = VideosAdapter(it.videos)
                    loading.gone()
                }

                else -> {}
            }
        })
    }

    companion object {
       fun createFragment(program: Program) : ProgramItemFragment {
         return ProgramItemFragment().apply {
                this.program = program
            }
       }
    }
}