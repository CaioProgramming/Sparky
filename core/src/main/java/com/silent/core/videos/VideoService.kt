package com.silent.core.videos

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.silent.core.utils.VIDEOS_PATH
import com.silent.ilustriscore.BuildConfig
import com.silent.ilustriscore.core.bean.BaseBean
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.utilities.SEARCH_SUFFIX
import kotlinx.coroutines.tasks.await

class VideoService : BaseService() {

    override val dataPath = VIDEOS_PATH
    override var isDebug = true

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

    suspend fun getPodcastVideos(podcastId: String) = query(podcastId, "podcastId")

}