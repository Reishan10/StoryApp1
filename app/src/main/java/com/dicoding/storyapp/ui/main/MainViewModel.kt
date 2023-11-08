package com.dicoding.storyapp.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.UserDataStorePreferences
import com.dicoding.storyapp.data.model.CreateStoryResponse
import com.dicoding.storyapp.data.model.ListStoryItem
import com.dicoding.storyapp.data.model.StoryResponse
import com.dicoding.storyapp.network.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainViewModel(private val pref: UserDataStorePreferences) : ViewModel() {

    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList: LiveData<List<ListStoryItem>> = _storyList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "MainViewModel"
    }


    fun getListStory(token: String) {

        _isLoading.value = true
        val client = ApiConfig.getApiService().getListStory(bearer = "Bearer ${token}")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _storyList.value = response.body()?.listStory
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun postNewStory(token: String, imageFile: File, desc: String) {
        _isLoading.value = true

        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        val client = ApiConfig.getApiService()
            .postNewStory(bearer = "Bearer ${token}", imageMultipart, description)
        client.enqueue(object : Callback<CreateStoryResponse> {
            override fun onResponse(
                call: Call<CreateStoryResponse>,
                response: Response<CreateStoryResponse>
            ) {
                _isLoading.value = false
                when (response.code()) {
                    401 -> "${response.code()} : Bad Request"
                    403 -> "${response.code()} : Forbidden"
                    404 -> "${response.code()} : Not Found"
                    else -> "${response.code()} : ${response.message()}"
                }
            }

            override fun onFailure(call: Call<CreateStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }


}