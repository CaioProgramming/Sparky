package com.silent.core.videos

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.silent.core.utils.VIDEOS_PATH
import com.silent.ilustriscore.core.bean.BaseBean
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.tasks.await

class VideoService : BaseService() {

    override val dataPath = VIDEOS_PATH
    override var offlineEnabled = true
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

    suspend fun getHomeVideos(podcastId: String): ServiceResult<DataException, ArrayList<BaseBean>> {
        if (isDebug) {
            Log.i(
                javaClass.simpleName,
                "query: searching for $podcastId at field on collection $dataPath with limit -> 10"
            )
        }
        if (requireAuth && currentUser() == null) return ServiceResult.Error(DataException.AUTH)
        val query = reference.whereEqualTo("podcastId", podcastId)
            .orderBy("publishedAt", Query.Direction.DESCENDING).limit(10).get().await().documents
        return if (query.isNotEmpty()) {
            ServiceResult.Success(getDataList(query))
        } else {
            ServiceResult.Error(DataException.NOTFOUND)
        }
    }

}