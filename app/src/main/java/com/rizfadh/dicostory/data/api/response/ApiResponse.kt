package com.rizfadh.dicostory.data.api.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class ApiResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("loginResult")
	val loginResult: LoginResult? = null,

	@field:SerializedName("listStory")
	val listStory: List<StoryResult>? = null,

	@field:SerializedName("story")
	val story: StoryResult? = null
)

data class LoginResult(
	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("token")
	val token: String
)

@Entity(tableName = "story")
data class StoryResult(
	@PrimaryKey
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("lat")
	val lat: Double? = null,

	@field:SerializedName("lon")
	val lon: Double? = null
)