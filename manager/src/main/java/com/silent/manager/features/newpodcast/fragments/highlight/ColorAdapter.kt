package com.silent.manager.features.newpodcast.fragments.highlight

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.utils.ColorUtils
import com.silent.manager.R
import com.silent.manager.databinding.ColorCardBinding

class ColorAdapter(context: Context, private val onColorPick: (String) -> Unit) :
    RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    val colors = ColorUtils.getColors(context)

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            ColorCardBinding.bind(itemView).run {
                colorcard.setCardBackgroundColor(Color.parseColor(colors[bindingAdapterPosition]))
                colorcard.setOnClickListener {
                    onColorPick(colors[bindingAdapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.color_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = colors.size


}