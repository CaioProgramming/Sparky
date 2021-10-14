package com.silent.core

import com.silent.ilustriscore.core.model.BaseModel
import com.silent.ilustriscore.core.presenter.BasePresenter
import com.silent.ilustriscore.core.view.BaseView

class ProgramPresenter(override val view: BaseView<Program>): BasePresenter<Program>() {
    override val model = ProgramModel(this)
}