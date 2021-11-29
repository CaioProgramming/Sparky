package com.silent.core.podcast

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult

class PodcastService : BaseService<Podcast>() {

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

    //TODO REMOVE SAMPLE DATA RETURN
    override suspend fun getAllData(): ServiceResult<DataException, ArrayList<Podcast>> {
        return ServiceResult.Success(SampleData.programs())
    }

    override val dataPath = "Podcasts"

    override suspend fun getSingleData(id: String): ServiceResult<DataException, Podcast> {
        val isInt = id.toIntOrNull() != null
        return if (!isInt) {
            super.getSingleData(id)
        } else {
            ServiceResult.Success(SampleData.programs().find { it.id == id }!!)
        }
    }
}