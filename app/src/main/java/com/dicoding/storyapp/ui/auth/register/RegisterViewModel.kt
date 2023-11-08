package com.dicoding.storyapp.ui.auth.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.UserDataStorePreferences
import com.dicoding.storyapp.data.model.RegisterResponse
import com.dicoding.storyapp.network.ApiConfig
import com.dicoding.storyapp.ui.auth.login.LoginViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref: UserDataStorePreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val error = MutableLiveData("")
    val message = MutableLiveData("")
    private val TAG = LoginViewModel::class.simpleName

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                when (response.code()) {
                    400 -> error.postValue("400")
                    201 -> message.postValue("201")
                    else -> error.postValue("ERROR ${response.code()} : ${response.errorBody()}")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }
}