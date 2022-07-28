package com.silent.manager.features.newpodcast.fragments.hosts

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.silent.core.component.GroupType
import com.silent.core.podcast.Host
import com.silent.manager.R
import com.silent.manager.databinding.HostDialogBinding

class HostDialog : BottomSheetDialogFragment() {

    private lateinit var groupType: GroupType
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
        HostDialogBinding.bind(view).run {
            Glide.with(requireContext()).load(host.profilePic)
                .error(R.drawable.ic_iconmonstr_connection_1).into(hostIcon)
            message.text = "Tem certeza que deseja adicionar esse host?"
            username.setText(host.name)
            hostSocialLink.addTextChangedListener {

                val icon = if (Patterns.WEB_URL.matcher(it.toString())
                        .matches()
                ) R.drawable.ic_round_check_24 else R.drawable.ic_baseline_close_24
                hostSocialLink.setCompoundDrawables(
                    null,
                    null,
                    AppCompatResources.getDrawable(requireContext(), icon),
                    null
                )
            }
            confirmHostButton.setOnClickListener {
                host.name = username.text.toString()
                host.description = hostDescription.text.toString()
                host.socialUrl = hostSocialLink.text.toString()
                if (hostSocialLink.text.isNotEmpty()) {
                    if (!Patterns.WEB_URL.matcher(hostSocialLink.text.toString()).matches()) {
                        MaterialAlertDialogBuilder(requireContext()).setTitle("Atenção")
                            .setMessage("O link que inseriu é inválido, deseja continuar mesmo assim?")
                            .setPositiveButton("Continuar") { i, _ ->
                                continueClick.invoke()
                                i.dismiss()
                            }
                            .setNegativeButton("Cancelar") { i, _ ->
                                i.dismiss()
                            }
                            .show()
                    }
                } else {
                    continueClick.invoke()
                    dialog?.dismiss()
                }
            }
        }

    }

    companion object {
        fun getInstance(type: GroupType, host: Host, onContinue: () -> Unit): HostDialog {
            return HostDialog().apply {
                this.host = host
                this.continueClick = onContinue
                this.groupType = type
            }
        }
    }
}