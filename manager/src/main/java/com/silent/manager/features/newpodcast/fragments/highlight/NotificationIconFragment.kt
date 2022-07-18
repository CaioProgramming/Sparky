package com.silent.manager.features.newpodcast.fragments.highlight

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.silent.manager.R
import com.silent.manager.databinding.HighlightColorDialogBinding

const val NOTIFICATION_ICON_TAG = "NOTIFICATION_ICON_FRAG"

class NotificationIconFragment : BottomSheetDialogFragment() {

    var highLightColor: Int = Color.WHITE
    lateinit var onIconPick: (String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.highlight_color_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HighlightColorDialogBinding.bind(view).run {
            message.text = "Selecione um ícone para representar o podcast"
            highlightColors.layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
            highlightColors.adapter = NotificationIconAdapter(highLightColor) {
                onIconPick(it)
                dismiss()
            }
        }
    }

    companion object {
        fun getInstance(highLColor: Int, iconPick: (String) -> Unit): NotificationIconFragment {
            return NotificationIconFragment().apply {
                highLightColor = highLColor
                onIconPick = iconPick
            }
        }
    }

}