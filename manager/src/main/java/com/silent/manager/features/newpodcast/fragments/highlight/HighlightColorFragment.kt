package com.silent.manager.features.newpodcast.fragments.highlight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.silent.manager.R
import com.silent.manager.databinding.HighlightColorDialogBinding

const val HIGHLIGHT_TAG = "HIGHLIGHTCOLORFRAG"

class HighlightColorFragment : BottomSheetDialogFragment() {

    lateinit var onColorPick: (String) -> Unit

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
            highlightColors.adapter = ColorAdapter(requireContext()) {
                onColorPick(it)
                dismiss()
            }
        }
    }

    companion object {
        fun getInstance(colorPick: (String) -> Unit): HighlightColorFragment {
            return HighlightColorFragment().apply {
                onColorPick = colorPick
            }
        }
    }

}