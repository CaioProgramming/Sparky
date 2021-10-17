package com.silent.sparky.programs

import com.silent.core.program.Program
import com.silent.core.program.ProgramPresenter
import com.silent.ilustriscore.core.view.BaseView
import com.silent.sparky.databinding.ActivityMainBinding
import com.silent.sparky.program.ProgramActivity

class MainActBinder(override val viewBind: ActivityMainBinding) : BaseView<Program>() {
    override val presenter = ProgramPresenter(this)

    override fun initView() {
        val data = SampleData.programs()
        viewBind.programsRecycler.run {
            adapter = ProgramsAdapter(data) {
                ProgramActivity.getLaunchIntent(it, context)
            }
        }
        presenter.loadData()
    }

    override fun showListData(list: List<Program>) {
        super.showListData(list)

    }
}