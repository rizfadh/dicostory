package com.rizfadh.dicostory.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.rizfadh.dicostory.data.api.response.ApiResponse
import com.rizfadh.dicostory.data.repository.UserRepository
import com.rizfadh.dicostory.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class AddStoryViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<ApiResponse>>()
    val uploadResult: LiveData<Result<ApiResponse>> = _uploadResult

    fun addStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) {
        _uploadResult.value = Result.Loading
        val request = if (lat == null && lon == null) userRepository.addStory(
            token,
            description,
            photo
        ) else userRepository.addStoryLocation(
            token,
            description,
            photo,
            lat as RequestBody,
            lon as RequestBody
        )
        request.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: retrofit2.Response<ApiResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && !result.error) _uploadResult.value =
                        Result.Success(result)
                } else {
                    val result = response.errorBody()?.string()
                    val error: ApiResponse =
                        GsonBuilder().create().fromJson(result, ApiResponse::class.java)
                    _uploadResult.value = Result.Error(error.message)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _uploadResult.value = Result.Error(t.message.toString())
            }

        })
    }
}