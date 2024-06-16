package com.example.agronect.data.response

import com.google.gson.annotations.SerializedName

data class UpdateProfilePhotoResponse(

	@field:SerializedName("dataUploadProfile")
	val dataUploadProfile: DataUploadProfile? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataUploadProfile(

	@field:SerializedName("photoProfileUrl")
	val photoProfileUrl: String? = null
)
