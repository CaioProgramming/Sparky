package com.silent.core.program

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.silent.ilustriscore.core.model.BaseModel
import com.silent.ilustriscore.core.presenter.BasePresenter

class ProgramModel(presenter: BasePresenter<Program>) : BaseModel<Program>(
    presenter
) {
    override val path = "Programs"

    override fun deserializeDataSnapshot(dataSnapshot: DocumentSnapshot): Program {
       return dataSnapshot.toObject(Program::class.java)!!.apply {
           id = dataSnapshot.id
       }
    }

    override fun deserializeDataSnapshot(dataSnapshot: QueryDocumentSnapshot): Program {
       return dataSnapshot.toObject(Program::class.java).apply {
           id = dataSnapshot.id
       }
    }
}