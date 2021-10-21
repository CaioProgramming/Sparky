package com.silent.sparky.programs

import androidx.fragment.app.FragmentManager
import com.silent.core.program.Program
import com.silent.core.program.ProgramPresenter
import com.silent.ilustriscore.core.view.BaseView
import com.silent.sparky.databinding.ActivityMainBinding
import com.silent.sparky.program.ProgramActivity
import com.silent.sparky.program.adapter.ChannelHeaderAdapter

class MainActBinder(override val viewBind: ActivityMainBinding,
                    private val fragmentManager: FragmentManager) : BaseView<Program>() {

    override val presenter = ProgramPresenter(this)

    override fun initView() {
        showListData(SampleData.programs())
    }

    override fun showListData(list: List<Program>) {
        super.showListData(list)
        viewBind.programsRecycler.run {
            adapter = ProgramsAdapter(list) {
                ProgramActivity.getLaunchIntent(it, context)
            }
        }
        viewBind.channelEpsRecycler.adapter = ChannelHeaderAdapter(ArrayList(list), fragmentManager)
    }
}