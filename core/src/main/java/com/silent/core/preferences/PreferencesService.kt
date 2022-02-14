package com.silent.core.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult

class PreferencesService(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Sparky", Context.MODE_PRIVATE)

    private fun getEditor() = sharedPreferences.edit()

    suspend fun editPreference(
        key: String,
        values: Set<String>
    ): ServiceResult<DataException, String> {
        return try {
            Log.i(javaClass.simpleName, "editing preferences -> $key \n $values")
            getEditor().putStringSet(key, values).commit()
            ServiceResult.Success("Preferências atualizadas")
        } catch (e: Exception) {
            ServiceResult.Error(DataException.SAVE)
        }
    }

    suspend fun editPreference(key: String, value: Boolean): ServiceResult<DataException, String> {
        return try {
            getEditor().putBoolean(key, value).commit()
            ServiceResult.Success("Preferências atualizadas")
        } catch (e: Exception) {
            ServiceResult.Error(DataException.SAVE)
        }
    }

    suspend fun editPreference(key: String, value: String): ServiceResult<DataException, String> {
        return try {
            getEditor().putString(key, value).commit()
            ServiceResult.Success("Preferências atualizadas")
        } catch (e: Exception) {
            ServiceResult.Error(DataException.SAVE)
        }
    }

    suspend fun editPreference(key: String, value: Float): ServiceResult<DataException, String> {
        return try {
            getEditor().putFloat(key, value).commit()
            ServiceResult.Success("Preferências atualizadas")
        } catch (e: Exception) {
            ServiceResult.Error(DataException.SAVE)
        }
    }

    suspend fun editPreference(key: String, value: Int): ServiceResult<DataException, String> {
        return try {
            getEditor().putInt(key, value).commit()
            ServiceResult.Success("Preferências atualizadas")
        } catch (e: Exception) {
            ServiceResult.Error(DataException.SAVE)
        }
    }

    suspend fun editPreference(key: String, value: Long): ServiceResult<DataException, String> {
        return try {
            getEditor().putLong(key, value).commit()
            ServiceResult.Success("Preferências atualizadas")
        } catch (e: Exception) {
            ServiceResult.Error(DataException.SAVE)
        }
    }

    suspend fun getBooleanValue(key: String) = sharedPreferences.getBoolean(key, false)
    suspend fun getFloatValue(key: String) = sharedPreferences.getFloat(key, 0f)
    suspend fun getLongValue(key: String) = sharedPreferences.getLong(key, 0)
    suspend fun getStringValue(key: String) = sharedPreferences.getString(key, "")
    suspend fun getStringSetValue(key: String) = sharedPreferences.getStringSet(key, setOf())

}