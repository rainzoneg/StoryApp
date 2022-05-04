package com.dicoding.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.helper.ApiConfig
import com.dicoding.storyapp.model.PostResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel() : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _registerResponse = MutableLiveData<PostResponse>()
    val registerResponse : LiveData<PostResponse> = _registerResponse

    fun requestRegister(name: String, email: String, password:String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().registerRequest(name, email, password)
        client.enqueue(object: Callback<PostResponse> {
            override fun onResponse(
                call: Call<PostResponse>,
                response: Response<PostResponse>
            ) {
                _isLoading.value = false
                if(response.isSuccessful){
                    _registerResponse.value = response.body()
                } else{
                    if(response.code() == 400){
                        _registerResponse.value = PostResponse(true, "Email is already taken")
                    }
                }
            }
            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object{
        private const val TAG = "RegisterViewModel"
    }
}