package com.silent.core.program

import com.silent.ilustriscore.core.presenter.BasePresenter
import com.silent.ilustriscore.core.view.BaseView

class ProgramPresenter(override val view: BaseView<Program>): BasePresenter<Program>() {
    override val model = ProgramModel(this)
}