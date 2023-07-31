package com.rizfadh.dicostory.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.gson.GsonBuilder
import com.rizfadh.dicostory.data.api.response.ApiResponse
import com.rizfadh.dicostory.data.api.response.StoryResult
import com.rizfadh.dicostory.data.repository.UserRepository
import com.rizfadh.dicostory.utils.Result

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getStoriesMaps(token: String, page: Int, size: Int): LiveData<Result<List<StoryResult>>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = userRepository.getStoriesMaps(token, page, size)
                if (response.isSuccessful) {
                    response.body()?.let {
                        it.listStory?.let { stories ->
                            emit(Result.Success(stories))
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
}