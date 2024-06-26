package com.example.agronect.data.retrofit

import com.example.agronect.data.response.AddSharingResponse
import com.example.agronect.data.response.DetailSharingResponse
import com.example.agronect.data.response.GetAllSharingPagingResponse
import com.example.agronect.data.response.LoginResponse
import com.example.agronect.data.response.RegisterResponse
import com.example.agronect.data.response.UpdatePasswordResponse
import com.example.agronect.data.response.UpdateProfilePhotoResponse
import com.example.agronect.data.response.UpdateProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("signup")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("signin")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("sharing")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part("content") content: RequestBody,
        @Part imgUrl: MultipartBody.Part?
    ): Call<AddSharingResponse>

    @GET("sharing")
    fun getAllSharing(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Call<GetAllSharingPagingResponse>

//    @GET("stories")
//    fun getStories(
//        @Header("Authorization") token: String,
//        @Query("page") page: Int = 1,
//        @Query("size") size: Int = 10
//    ): Call<StoryResponse>
//
    @GET("sharing/{sharing_id}")
    fun getDetailSharing(
        @Header("Authorization") token: String,
        @Path("sharing_id") sharingid: String
    ): Call<DetailSharingResponse>

    @Multipart
    @PUT("users/update-users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @PartMap userInfo: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imgUrl: MultipartBody.Part? = null
    ): Response<UpdateProfileResponse>

    @PUT("users/change-password/{user_id}")
    fun changePassword(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String,
        @Body passwordChangeRequest: PasswordChangeRequest
    ): Call<UpdatePasswordResponse>

    @Multipart
    @POST("users/{user_id}/upload-photoprofile")
    fun uploadProfilePhoto(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String,
        @Part imgUrl: MultipartBody.Part?
        ): Call<UpdateProfilePhotoResponse>

//
//    @Multipart
//    @POST("stories")
//    fun uploadImage(
//        @Header("Authorization") token: String,
//        @Part file: MultipartBody.Part,
////        @Part("description") description: RequestBody,
//    ): Call<AddResponse>
//
//    @GET("stories")
//    fun getStoriesWithLocation(
//        @Header("Authorization") token: String,
//        @Query("location") location : Int = 1
//    ): Call<StoryResponse>
}

data class PasswordChangeRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)