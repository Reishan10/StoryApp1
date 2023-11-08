package com.dicoding.storyapp.network

import com.dicoding.storyapp.data.model.CreateStoryResponse
import com.dicoding.storyapp.data.model.LoginResponse
import com.dicoding.storyapp.data.model.RegisterResponse
import com.dicoding.storyapp.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("stories")
    fun getListStory(
        @Header("Authorization") bearer: String?
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun postNewStory(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
    ): Call<CreateStoryResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<RegisterResponse>


}