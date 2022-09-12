package com.silent.manager.features.newpodcast.fragments.highlight

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilustris.animations.slideInBottom
import com.silent.core.utils.ImageUtils
import com.silent.manager.R
import com.silent.manager.databinding.IconCardBinding

class NotificationIconAdapter(val highlightColor: Int, private val onIconPick: (String) -> Unit) :
    RecyclerView.Adapter<NotificationIconAdapter.IconViewHolder>() {

    val icons = ImageUtils.getIcons()

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            IconCardBinding.bind(itemView).run {
                val context = root.context
                val icon = icons[bindingAdapterPosition]
                iconCard.setImageDrawable(context.getDrawable(icon.drawable))
                iconCard.imageTintList = ColorStateList.valueOf(highlightColor)
                root.setOnClickListener {
                    onIconPick(icon.name)
                }
                root.contentDescription = icon.name
                root.slideInBottom()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        return IconViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.icon_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = icons.size


}