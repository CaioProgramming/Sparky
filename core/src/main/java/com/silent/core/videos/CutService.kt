package com.silent.core.videos

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.silent.core.utils.CUTS_PATH
import com.silent.ilustriscore.core.bean.BaseBean
import com.silent.ilustriscore.core.model.BaseService

class CutService: BaseService() {
    override val dataPath = CUTS_PATH

    override fun deserializeDataSnapshot(dataSnapshot: DocumentSnapshot): BaseBean? {
        return dataSnapshot.toObject(Video::class.java)?.apply {
            id = dataSnapshot.id
        }
    }

    override fun deserializeDataSnapshot(dataSnapshot: QueryDocumentSnapshot): BaseBean {
        return dataSnapshot.toObject(Video::class.java).apply {
            id = dataSnapshot.id
        }
    }

    suspend fun getPodcastCuts(podcastId: String) = query(podcastId, "podcastId")
}