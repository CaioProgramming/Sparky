package com.silent.core.podcast

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.silent.core.utils.PODCASTS_PATH
import com.silent.ilustriscore.BuildConfig
import com.silent.ilustriscore.core.model.BaseService

class PodcastService : BaseService() {

    override val dataPath = PODCASTS_PATH
    override var requireAuth = false
    override var isDebug = BuildConfig.DEBUG

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