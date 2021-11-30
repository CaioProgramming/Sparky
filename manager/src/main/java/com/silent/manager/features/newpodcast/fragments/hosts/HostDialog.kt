package com.silent.manager.features.newpodcast.fragments.hosts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.silent.core.podcast.Host
import com.silent.manager.R
import kotlinx.android.synthetic.main.host_dialog.*

class HostDialog : BottomSheetDialogFragment() {

    lateinit var host: Host
    lateinit var continueClick: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.host_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext()).load(host.profilePic).into(host_icon)
        username.text = "${host.name}(${host.user})"
        confirm_host_button.setOnClickListener {
            continueClick.invoke()
            dialog?.dismiss()
        }
    }

    companion object {
        fun getInstance(host: Host, onContinue: () -> Unit): HostDialog {
            return HostDialog().apply {
                this.host = host
                this.continueClick = onContinue
            }
        }
    }
}