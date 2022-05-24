package com.silent.manager.features.newpodcast.fragments.hosts

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.ilustris.animations.fadeIn
import com.silent.core.component.GroupType
import com.silent.core.podcast.Host
import com.silent.ilustriscore.core.utilities.DateFormats
import com.silent.ilustriscore.core.utilities.format
import com.silent.ilustriscore.core.utilities.formatDate
import com.silent.manager.R
import com.silent.manager.databinding.HostDialogBinding
import java.util.*

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
                .error(R.drawable.ic_iconmonstr_microphone_13).into(hostIcon)
            message.text =
                if (groupType == GroupType.GUESTS) "Tem certeza que deseja adicionar esse convidado?" else "Tem certeza que deseja adicionar esse host?"
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
            guestDateButton.setOnClickListener {
                openDatePicker()
            }
            if (groupType == GroupType.GUESTS) {
                guestDateButton.fadeIn()
            }
        }

    }

    private fun HostDialogBinding.openDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        DatePickerDialog(
            requireContext(),
            { view, year, month, dayOfMonth ->
                val hostDate = Calendar.getInstance()
                hostDate.set(year, month, dayOfMonth)
                TimePickerDialog(
                    requireContext(),
                    { view, hourOfDay, minute ->
                        hostDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        hostDate.set(Calendar.MINUTE, minute)
                        hostDate.set(Calendar.SECOND, 0)
                        val dateFormatted = hostDate.time.formatDate("dd.MM - HH") + "H"
                        guestDateButton.text = dateFormatted
                        host.comingDate = hostDate.time
                    }, hour, minute, android.text.format.DateFormat.is24HourFormat(requireContext())
                ).show()
            }, year, month, day
        ).show()

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