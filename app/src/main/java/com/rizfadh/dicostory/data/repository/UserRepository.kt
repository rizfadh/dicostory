package com.rizfadh.dicostory.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.rizfadh.dicostory.data.api.response.StoryResult
import com.rizfadh.dicostory.data.api.retrofit.ApiService
import com.rizfadh.dicostory.data.local.room.StoryDatabase
import com.rizfadh.dicostory.data.remotemediator.StoryRemoteMediator
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

    fun register(name: String, email: String, password: String) =
        apiService.register(name, email, password)

    fun login(email: String, password: String) = apiService.login(email, password)

    fun addStory(token: String, description: RequestBody, photo: MultipartBody.Part) =
        apiService.addStory(token, description, photo)

    fun addStoryLocation(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody,
        lon: RequestBody
    ) = apiService.addStoryLocation(token, description, photo, lat, lon)

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): LiveData<PagingData<StoryResult>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(token, storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getStoryDetail(token: String, userId: String) =
        apiService.getStoryDetail(token, userId)

    suspend fun getStoriesMaps(token: String, page: Int, size: Int) =
        apiService.getStories(token, page, size, 1)

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(storyDatabase: StoryDatabase, apiService: ApiService) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(storyDatabase, apiService).also {
                    INSTANCE = it
                }
            }
    }
}