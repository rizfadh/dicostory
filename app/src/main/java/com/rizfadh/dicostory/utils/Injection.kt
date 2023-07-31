package com.rizfadh.dicostory.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.rizfadh.dicostory.data.api.retrofit.ApiConfig
import com.rizfadh.dicostory.data.local.room.StoryDatabase
import com.rizfadh.dicostory.data.preference.SettingPreference
import com.rizfadh.dicostory.data.repository.PreferenceRepository
import com.rizfadh.dicostory.data.repository.UserRepository

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = SettingPreference.USER_DATA)

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        return UserRepository.getInstance(database, apiService)
    }

    fun providePreferenceRepository(context: Context): PreferenceRepository {
        val dataStore = context.datastore
        val preference = SettingPreference.getInstance(dataStore)
        return PreferenceRepository.getInstance(preference)
    }
}