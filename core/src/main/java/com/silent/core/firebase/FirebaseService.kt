package com.silent.core.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.tasks.await

private const val ALL_USERS_TOPIC = "SparkyUsers"
class FirebaseService {

    suspend fun generateFirebaseToken(): ServiceResult<DataException, String> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            ServiceResult.Success(token)
        } catch (e: Exception) {
            e.printStackTrace()
            ServiceResult.Error(DataException.UNKNOWN)
        }
    }

    suspend fun subscribeToTopic(topicName: String = ALL_USERS_TOPIC, serviceResult: (ServiceResult<DataException, String>) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(topicName).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                serviceResult(ServiceResult.Success("Subscribed to topic($topicName) successfull!"))
            } else {
                Log.e("FireService", "SubscribeTopic: Error -> ${task.exception}")
                serviceResult(ServiceResult.Error(DataException.UNKNOWN))
            }
        }
    }

    suspend fun unsubscribeTopic(topicName: String, serviceResult: (ServiceResult<DataException, String>) -> Unit) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                serviceResult(ServiceResult.Success("Unsubscribed to topic successfull!"))
            } else {
                Log.e("FireService", "UnsubscribeTopic: Error -> ${task.exception}")
                serviceResult(ServiceResult.Error(DataException.UNKNOWN))
            }
        }
    }
}