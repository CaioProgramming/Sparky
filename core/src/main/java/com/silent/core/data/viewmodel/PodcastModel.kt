package com.silent.core.data.viewmodel

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.silent.core.data.podcast.Podcast
import com.silent.ilustriscore.core.model.BaseModel
import com.silent.ilustriscore.core.presenter.BasePresenter

class PodcastModel(presenter: BasePresenter<Podcast>) : BaseModel<Podcast>(
    presenter
) {
    override val path = "Podcasts"

    override fun deserializeDataSnapshot(dataSnapshot: DocumentSnapshot): Podcast {
       return dataSnapshot.toObject(Podcast::class.java)!!.apply {
           id = dataSnapshot.id
       }
    }

    override fun deserializeDataSnapshot(dataSnapshot: QueryDocumentSnapshot): Podcast {
       return dataSnapshot.toObject(Podcast::class.java).apply {
           id = dataSnapshot.id
       }
    }
}