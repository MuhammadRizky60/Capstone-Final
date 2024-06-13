package com.example.test.data.response

import com.google.gson.annotations.SerializedName

data class DetailSharingResponse(

	@field:SerializedName("dataById")
	val dataById: DataById? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataById(

	@field:SerializedName("sharing_id")
	val sharingId: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("ImgUrl")
	val imgUrl: Any? = null,

	@field:SerializedName("content")
	val content: String? = null
)
