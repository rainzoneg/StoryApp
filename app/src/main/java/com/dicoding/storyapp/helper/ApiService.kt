package com.dicoding.storyapp.helper

import com.dicoding.storyapp.model.LoginResponse
import com.dicoding.storyapp.model.PostResponse
import com.dicoding.storyapp.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("stories")
    fun getStories(
        @Header("Authorization")value : String,
    ): Call<StoryResponse>

    @GET("stories")
    suspend fun getPagedStories(
        @Header("Authorization")value: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @GET("stories?location=1")
    fun getStoriesWithLocation(
        @Header("Authorization")value: String,
        @Query("size") size: Int = 200
    ): Call<StoryResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginRequest(
        @Field("email")email: String,
        @Field("password")password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun registerRequest(
        @Field("name")name: String,
        @Field("email")email: String,
        @Field("password")password: String
    ): Call<PostResponse>

    @Multipart
    @POST("stories")
    fun uploadRequest(
        @Header("Authorization")value : String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<PostResponse>
}