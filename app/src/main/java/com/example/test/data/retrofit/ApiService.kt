package com.example.test.data.retrofit

import com.example.test.data.response.AddSharingResponse
import com.example.test.data.response.DetailSharingResponse
import com.example.test.data.response.GetAllSharingResponse
import com.example.test.data.response.LoginResponse
import com.example.test.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<GetAllSharingResponse>

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