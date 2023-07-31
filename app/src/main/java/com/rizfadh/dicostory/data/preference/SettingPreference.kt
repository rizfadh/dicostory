package com.rizfadh.dicostory.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class SettingPreference private constructor(private val datastore: DataStore<Preferences>) {

    private val USER_TOKEN_KEY = stringPreferencesKey(USER_TOKEN)

    fun getUserToken() = datastore.data.map {
        it[USER_TOKEN_KEY]
    }

    suspend fun saveUserToken(token: String) {
        datastore.edit {
            it[USER_TOKEN_KEY] = token
        }
    }

    suspend fun deleteUserToken() {
        datastore.edit {
            it.remove(USER_TOKEN_KEY)
        }
    }

    companion object {
        private const val USER_TOKEN = "user_token"
        const val USER_DATA = "user_data"

        @Volatile
        private var INSTANCE: SettingPreference? = null

        fun getInstance(datastore: DataStore<Preferences>) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: SettingPreference(datastore).also {
                INSTANCE = it
            }
        }
    }
}