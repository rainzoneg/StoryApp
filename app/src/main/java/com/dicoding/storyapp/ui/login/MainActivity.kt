package com.dicoding.storyapp.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.helper.SettingsPreferences
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.register.RegisterActivity
import com.dicoding.storyapp.ui.story.ListStoryActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val pref = SettingsPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application, pref)).get(
            MainViewModel::class.java
        )

        mainViewModel.getSessionInfo().observe(this){ sessionData ->
            if(sessionData.trim() != ""){
                val intent = Intent(this@MainActivity, ListStoryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        mainViewModel.loginResponse.observe(this){ loginData ->
            if(loginData.loginResult == null && loginData.error){
                Toast.makeText(this, loginData.message, Toast.LENGTH_SHORT).show()
            }else{
                mainViewModel.saveSessionInfo(loginData.loginResult!!.name, loginData.loginResult.token)
                val intent = Intent(this@MainActivity, ListStoryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        mainViewModel.isLoading.observe(this){
            showLoading(it)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if(email.isEmpty()) {
                binding.etEmail.error = "Email should not be empty!"
            }else if(password.isEmpty()){
                binding.etPassword.error = "Password should not be empty!"
            }else if(binding.etPassword.validity){
                mainViewModel.requestLogin(email, password)
            }
        }
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.localizationLogin.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBarLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}