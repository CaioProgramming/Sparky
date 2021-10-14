package com.silent.sparky.programs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silent.core.Program
import com.silent.core.databinding.ProgramIconLayoutBinding
import com.silent.sparky.R

class ProgramsAdapter(val programs: List<Program>): RecyclerView.Adapter<ProgramsAdapter.ProgramViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val programIconLayoutBinding: ProgramIconLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.program_icon_layout, parent, false)
        return ProgramViewHolder(programIconLayoutBinding)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = programs.size

    inner class ProgramViewHolder(private val programIconLayoutBinding: ProgramIconLayoutBinding):
        RecyclerView.ViewHolder(programIconLayoutBinding.root) {

        fun bind() {
            val context = programIconLayoutBinding.root.context
            programs[adapterPosition].run {
                Glide.with(context).load(iconURL).into(programIconLayoutBinding.programIcon)
                programIconLayoutBinding.programName.text = name
            }
        }

    }

}