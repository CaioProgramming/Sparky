package com.silent.core.podcast

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.silent.ilustriscore.core.model.BaseService

class PodcastService : BaseService() {

    override val dataPath = "Podcasts"
    override var requireAuth = false

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