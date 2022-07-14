package com.silent.core.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.tasks.await

class FirebaseService {

    suspend fun generateFirebaseToken() : ServiceResult<DataException, String> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            ServiceResult.Success(token)
        } catch (e: Exception) {
            e.printStackTrace()
            ServiceResult.Error(DataException.UNKNOWN)
        }
    }

}