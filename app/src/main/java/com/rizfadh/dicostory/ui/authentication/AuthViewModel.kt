package com.rizfadh.dicostory.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.gson.GsonBuilder
import com.rizfadh.dicostory.data.api.response.ApiResponse
import com.rizfadh.dicostory.data.repository.PreferenceRepository
import com.rizfadh.dicostory.data.repository.UserRepository
import com.rizfadh.dicostory.utils.Result
import retrofit2.Call
import retrofit2.Callback

class AuthViewModel(
    private val userRepository: UserRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<ApiResponse>>()
    val loginResult: LiveData<Result<ApiResponse>> = _loginResult

    private val _registerResult = MutableLiveData<Result<ApiResponse>>()
    val registerResult: LiveData<Result<ApiResponse>> = _registerResult

    fun register(name: String, email: String, password: String) {
        _registerResult.value = Result.Loading
        userRepository.register(name, email, password).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && !result.error) _registerResult.value =
                        Result.Success(result)
                } else {
                    val result = response.errorBody()?.string()
                    val error: ApiResponse = GsonBuilder().create().fromJson(result, ApiResponse::class.java)
                    _registerResult.value = Result.Error(error.message)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _registerResult.value = Result.Error(t.message.toString())
            }
        })
    }

    fun login(email: String, password: String) {
        _loginResult.value = Result.Loading
        userRepository.login(email, password).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && !result.error) _loginResult.value = Result.Success(result)
                } else {
                    val result = response.errorBody()
                    result?.let {
                        val error: ApiResponse =
                            GsonBuilder().create().fromJson(it.string(), ApiResponse::class.java)
                        _loginResult.value = Result.Error(error.message)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _loginResult.value = Result.Error(t.message.toString())
            }
        })
    }

    fun saveUserToken(token: String): LiveData<Result<String>> = liveData {
        emit(Result.Loading)
        preferenceRepository.saveUserToken(token)
        emit(Result.Success(""))
    }
}