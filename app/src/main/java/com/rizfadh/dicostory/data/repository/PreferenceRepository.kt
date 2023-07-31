package com.rizfadh.dicostory.data.repository

import com.rizfadh.dicostory.data.preference.SettingPreference

class PreferenceRepository private constructor(private val preference: SettingPreference) {

    fun getUserToken() = preference.getUserToken()

    suspend fun saveUserToken(token: String) = preference.saveUserToken(token)

    suspend fun deleteUserToken() = preference.deleteUserToken()

    companion object {
        @Volatile
        private var INSTANCE: PreferenceRepository? = null

        fun getInstance(preference: SettingPreference): PreferenceRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceRepository(preference).also {
                    INSTANCE = it
                }
            }
    }
}