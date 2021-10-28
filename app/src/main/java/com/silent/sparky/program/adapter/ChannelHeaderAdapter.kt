package com.silent.sparky.program.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.silent.core.program.Program
import com.silent.sparky.R
import com.silent.sparky.databinding.ChannelGroupLayoutBinding
import com.silent.sparky.program.ProgramItemFragment

private const val CHANNEL_HEADER_TAG = "CHANNEL_TAG_"
class ChannelHeaderAdapter(val programs: List<Program>,
                           val fragmentManager: FragmentManager): RecyclerView.Adapter<ChannelHeaderAdapter.ChannelHeaderViewHolder>() {

    inner class ChannelHeaderViewHolder(private val videoGroupLayoutBinding: ChannelGroupLayoutBinding) : RecyclerView.ViewHolder(videoGroupLayoutBinding.root) {
        fun bind() {
            programs[adapterPosition].run {
                fragmentManager
                    .beginTransaction()
                    .replace(videoGroupLayoutBinding.root.id,ProgramItemFragment.createFragment(this), CHANNEL_HEADER_TAG + this.name)
                    .commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelHeaderViewHolder {
        val bind = DataBindingUtil.inflate<ChannelGroupLayoutBinding>(LayoutInflater.from(parent.context), R.layout.channel_group_layout, parent, false)
        return ChannelHeaderViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ChannelHeaderViewHolder, position: Int) {
       holder.bind()
    }

    override fun getItemCount() = programs.count()

}