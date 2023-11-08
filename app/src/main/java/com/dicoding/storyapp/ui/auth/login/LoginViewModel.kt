package com.dicoding.storyapp.ui.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.UserDataStorePreferences
import com.dicoding.storyapp.data.model.LoginResponse
import com.dicoding.storyapp.data.model.LoginResult
import com.dicoding.storyapp.network.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val userPreferences: UserDataStorePreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val error = MutableLiveData("")
    val message = MutableLiveData("")
    private val TAG = LoginViewModel::class.simpleName

    val loginResponse = MutableLiveData<LoginResponse>()

    fun getUserData(): LiveData<LoginResult> {
        return userPreferences.getUser().asLiveData()
    }

    fun saveUserData(userName: String, userId: String, userToken: String) {
        viewModelScope.launch {
            userPreferences.saveUser(userName, userId, userToken)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.logout()
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val apiService = ApiConfig.getApiService()
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                when (response.code()) {
                    200 -> {
                        loginResponse.postValue(response.body())
                        message.postValue("200")
                    }

                    400 -> error.postValue("400")
                    401 -> error.postValue("401")
                    else -> error.postValue("ERROR ${response.code()} : ${response.message()}")
                }

                _isLoading.value = false
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }
}
