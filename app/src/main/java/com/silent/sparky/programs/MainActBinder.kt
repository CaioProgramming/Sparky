package com.silent.sparky.programs

import com.silent.core.Program
import com.silent.core.ProgramPresenter
import com.silent.ilustriscore.core.view.BaseView
import com.silent.sparky.databinding.ActivityMainBinding

class MainActBinder(override val viewBind: ActivityMainBinding) : BaseView<Program>() {
    override val presenter = ProgramPresenter(this)

    override fun initView() {
        val data = SampleData.programs()
        viewBind.programsRecycler.run {
            adapter = ProgramsAdapter(data)
        }
        presenter.loadData()
    }

    override fun showListData(list: List<Program>) {
        super.showListData(list)

    }
}