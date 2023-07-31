package com.rizfadh.dicostory.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rizfadh.dicostory.data.repository.PreferenceRepository
import com.rizfadh.dicostory.data.repository.UserRepository
import com.rizfadh.dicostory.ui.addstory.AddStoryViewModel
import com.rizfadh.dicostory.ui.authentication.AuthViewModel
import com.rizfadh.dicostory.ui.main.MainViewModel
import com.rizfadh.dicostory.ui.maps.MapsViewModel

class ViewModelFactory private constructor(
    private val userRepository: UserRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                return AuthViewModel(userRepository, preferenceRepository) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                return MainViewModel(userRepository, preferenceRepository) as T
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) ->
                return AddStoryViewModel(userRepository) as T
            modelClass.isAssignableFrom(MapsViewModel::class.java) ->
                return MapsViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory = INSTANCE ?: synchronized(this) {
            INSTANCE ?: ViewModelFactory(
                Injection.provideUserRepository(context),
                Injection.providePreferenceRepository(context)
            ).also {
                INSTANCE = it
            }
        }
    }
}