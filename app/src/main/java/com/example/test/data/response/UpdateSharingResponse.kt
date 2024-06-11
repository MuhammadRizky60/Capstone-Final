package com.example.test.data.response

import com.google.gson.annotations.SerializedName

data class UpdateSharingResponse(

	@field:SerializedName("dataUpdate")
	val dataUpdate: DataUpdate? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

//data class DataUpdate(
//
//	@field:SerializedName("sharing_id")
//	val sharingId: String? = null,
//
//	@field:SerializedName("imgUrl")
//	val imgUrl: Any? = null,
//
//	@field:SerializedName("content")
//	val content: String? = null
//)
