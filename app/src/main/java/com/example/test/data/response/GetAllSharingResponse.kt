package com.example.test.data.response

import com.google.gson.annotations.SerializedName

data class GetAllSharingResponse(

	@field:SerializedName("dataGetAll")
	val dataGetAll: List<DataGetAllItemItem?> = emptyList(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataGetAllItemItem(

	@field:SerializedName("sharing_id")
	val sharingId: String? = null,

	@field:SerializedName("imgUrl")
	val imgUrl: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)
