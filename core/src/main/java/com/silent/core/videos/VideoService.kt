package com.silent.core.videos

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.silent.core.utils.VIDEOS_PATH
import com.silent.ilustriscore.core.bean.BaseBean
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.utilities.SEARCH_SUFFIX
import kotlinx.coroutines.tasks.await

class VideoService : BaseService() {

    override val dataPath = VIDEOS_PATH

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

    suspend fun getHomeVideos(podcastId: String): ServiceResult<DataException, ArrayList<Video>> {
        Log.i(
            javaClass.simpleName,
            "query: Buscando por $podcastId em podcastID na collection $dataPath"
        )
        if (requireAuth && currentUser() == null) return ServiceResult.Error(DataException.AUTH)
        val query = reference.orderBy("podcastId")
            .startAt(podcastId)
            .endAt(podcastId + SEARCH_SUFFIX).get().await().documents
        return if (query.isNotEmpty()) {
            ServiceResult.Success(getDataList(query) as ArrayList<Video>)
        } else {
            ServiceResult.Error(DataException.NOTFOUND)
        }
    }

}