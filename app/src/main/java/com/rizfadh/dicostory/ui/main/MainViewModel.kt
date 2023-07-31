package com.rizfadh.dicostory.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.GsonBuilder
import com.rizfadh.dicostory.data.api.response.ApiResponse
import com.rizfadh.dicostory.data.api.response.StoryResult
import com.rizfadh.dicostory.data.repository.PreferenceRepository
import com.rizfadh.dicostory.data.repository.UserRepository
import com.rizfadh.dicostory.utils.Result
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    fun getStories(token: String): LiveData<PagingData<StoryResult>> =
        userRepository.getStories(token).cachedIn(viewModelScope)

    fun getStoryDetail(token: String, userId: String): LiveData<Result<StoryResult>> = liveData {
        emit(Result.Loading)
        try {
            val response = userRepository.getStoryDetail(token, userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    it.story?.let { story ->
                        emit(Result.Success(story))
                    } ?: run { emit(Result.Empty) }
                }
            } else {
                val result = response.errorBody()?.string()
                val error: ApiResponse =
                    GsonBuilder().create().fromJson(result, ApiResponse::class.java)
                emit(Result.Error(error.message))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getUserToken(): LiveData<String?> = preferenceRepository.getUserToken().asLiveData()

    fun deleteUserToken() {
        viewModelScope.launch {
            preferenceRepository.deleteUserToken()
        }
    }
}