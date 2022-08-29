package com.silent.core.component

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.R
import com.silent.core.databinding.HostGroupCardBinding
import com.silent.core.podcast.Host


class HostGroupAdapter(
    val groups: List<HostGroup>,
    val isEdit: Boolean = false,
    val hostSelected: (Host, GroupType) -> Unit,
    val highLightColor: String? = null
) :
    RecyclerView.Adapter<HostGroupAdapter.HostGroupViewHolder>() {


    inner class HostGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            val hostGroup = groups[bindingAdapterPosition]
            HostGroupCardBinding.bind(itemView).run {
                val gridLayoutManager =
                    GridLayoutManager(root.context, 2, RecyclerView.VERTICAL, false)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (hostGroup.hosts.size % 2 == 0) {
                            1
                        } else {
                            if (position == hostGroup.hosts.lastIndex) {
                                2
                            } else {
                                1
                            }
                        }
                    }
                }
                groupTitle.text = hostGroup.title
                highLightColor?.let {
                    groupTitle.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it))
                }
                guestsRecycler.adapter = HostAdapter(ArrayList(hostGroup.hosts), isEdit, {
                    hostSelected(it, hostGroup.groupType)
                }, groupType = hostGroup.groupType, highLightColor = highLightColor)
                guestsRecycler.layoutManager = gridLayoutManager
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostGroupViewHolder {
        return HostGroupViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.host_group_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HostGroupViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = groups.size

}