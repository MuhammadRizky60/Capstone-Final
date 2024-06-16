package com.example.agronect.data.response

import com.google.gson.annotations.SerializedName

data class GetAllSharingPagingResponse(

	@field:SerializedName("pagination")
	val pagination: Pagination? = null,

	@field:SerializedName("data")
	val data: List<DataGetAllItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Pagination(

	@field:SerializedName("totalItems")
	val totalItems: Int? = null,

	@field:SerializedName("itemsPerPage")
	val itemsPerPage: Int? = null,

	@field:SerializedName("totalPages")
	val totalPages: Int? = null,

	@field:SerializedName("currentPage")
	val currentPage: Int? = null
)

data class DataGetAllItem(

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
