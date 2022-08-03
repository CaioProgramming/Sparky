package com.silent.core.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.silent.core.BuildConfig
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.tasks.await

private const val ALL_USERS_TOPIC = "SparkyUsers"
class FirebaseService {

    suspend fun generateFirebaseToken(): ServiceResult<DataException, String> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            if (BuildConfig.DEBUG) {
                Log.d(
                    javaClass.simpleName,
                    "generateFirebaseToken: Firebase token generated successfuly"
                )
            }
            ServiceResult.Success(token)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
                Log.e(javaClass.simpleName, "generateFirebaseToken: Error ${e.message}")
            }
            ServiceResult.Error(DataException.UNKNOWN)
        }
    }

    suspend fun subscribeToTopic(
        topicName: String = ALL_USERS_TOPIC,
        serviceResult: ((ServiceResult<DataException, String>) -> Unit)? = null
    ) {
        FirebaseMessaging.getInstance().subscribeToTopic(topicName).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (BuildConfig.DEBUG) {
                    Log.d(
                        javaClass.simpleName,
                        "subscribeToTopic: Subscribed to topic($topicName) successfull!"
                    )
                }
                serviceResult?.invoke(ServiceResult.Success("Subscribed to topic($topicName) successfull!"))
            } else {
                if (BuildConfig.DEBUG) {
                    Log.e("FireService", "SubscribeTopic: Error -> ${task.exception}")
                }
                serviceResult?.invoke(ServiceResult.Error(DataException.UNKNOWN))
            }
        }
    }

    suspend fun unsubscribeTopic(topicName: String, serviceResult: (ServiceResult<DataException, String>) -> Unit) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (BuildConfig.DEBUG) {
                    Log.d(javaClass.simpleName, "unsubscribeTopic: unsubscribe successful")
                }
                serviceResult(ServiceResult.Success("Unsubscribed to topic successful!"))
            } else {
                if (BuildConfig.DEBUG) {
                    Log.e("FireService", "UnsubscribeTopic: Error -> ${task.exception}")
                }
                serviceResult(ServiceResult.Error(DataException.UNKNOWN))
            }
        }
    }
}