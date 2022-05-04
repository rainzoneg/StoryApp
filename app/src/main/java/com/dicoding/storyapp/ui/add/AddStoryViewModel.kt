package com.dicoding.storyapp.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.helper.ApiConfig
import com.dicoding.storyapp.helper.SettingsPreferences
import com.dicoding.storyapp.model.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val pref: SettingsPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _uploadResponse = MutableLiveData<PostResponse>()
    val uploadResponse : LiveData<PostResponse> = _uploadResponse

    fun uploadStory(value: String, description: RequestBody, lat: RequestBody?, lon: RequestBody?, imageMultipart: MultipartBody.Part){
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadRequest("Bearer $value", imageMultipart, description, lat, lon)
        client.enqueue(object : Callback<PostResponse> {
            override fun onResponse(
                call: Call<PostResponse>,
                response: Response<PostResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _uploadResponse.value = responseBody as PostResponse
                    }
                } else {
                    if(response.code() == 400){
                        _uploadResponse.value = PostResponse(true, "Description can't be empty.")
                    }else{
                        _uploadResponse.value = PostResponse(true, "Error uploading to server.")
                    }
                }
            }
            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                _isLoading.value = false
                _uploadResponse.value = PostResponse(true, "Error uploading to server.")
            }
        })
    }

    fun getSessionToken() : LiveData<String>{
        return pref.getSessionToken().asLiveData()
    }
}