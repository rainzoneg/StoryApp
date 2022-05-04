package com.dicoding.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.storyapp.helper.ApiConfig
import com.dicoding.storyapp.helper.SettingsPreferences
import com.dicoding.storyapp.model.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: SettingsPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse : LiveData<LoginResponse> = _loginResponse

    fun requestLogin(email: String, password: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().loginRequest(email, password)
        client.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if(response.isSuccessful){
                    _loginResponse.value = response.body()
                } else{
                    if(response.code() == 401){
                        _loginResponse.value = LoginResponse(null, true, "Invalid Password")
                    }else{
                        _loginResponse.value = LoginResponse(null, true, "Unknown Error")
                    }
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getSessionInfo() : LiveData<String>{
        return pref.getSessionInfo().asLiveData()
    }

    fun saveSessionInfo(username: String, token: String){
        viewModelScope.launch {
            pref.saveSessionInfo(username, token)
        }
    }

    companion object{
        private const val TAG = "MainViewModel"
    }

}