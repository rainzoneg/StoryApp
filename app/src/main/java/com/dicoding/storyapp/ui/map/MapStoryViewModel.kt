package com.dicoding.storyapp.ui.map

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.storyapp.helper.ApiConfig
import com.dicoding.storyapp.helper.SettingsPreferences
import com.dicoding.storyapp.model.ListStoryItem
import com.dicoding.storyapp.model.StoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapStoryViewModel(private val pref: SettingsPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory : LiveData<List<ListStoryItem>> = _listStory

    fun getStoryWithLocation(value: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStoriesWithLocation("Bearer $value")
        client.enqueue(object: Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if(response.isSuccessful){
                    val responseBody = response.body()
                    _listStory.value = responseBody?.listStory
                } else{
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getSessionToken() : LiveData<String> {
        return pref.getSessionToken().asLiveData()
    }

    companion object{
        private const val TAG = "ListStoryViewModel"
    }
}