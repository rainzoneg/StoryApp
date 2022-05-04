package com.dicoding.storyapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.ui.login.MainActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        registerViewModel.isLoading.observe(this){
            showLoading(it)
        }

        registerViewModel.registerResponse.observe(this){ registerData ->
            if(registerData.error){
                Toast.makeText(this, registerData.message, Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Registration succesful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnRegisterSubmit.setOnClickListener {
            val name = binding.etNameRegister.text.toString().trim()
            val email = binding.etEmailRegister.text.toString().trim()
            val password = binding.etPasswordRegister.text.toString().trim()
            if(email.isEmpty()){
                binding.etEmailRegister.error = "Email should not be empty!"
            }else if(name.isEmpty()){
                binding.etNameRegister.error = "Name should not be empty!"
            }else if(password.isEmpty()){
                binding.etPasswordRegister.error = "Password should not be empty!"
            }else{
                registerViewModel.requestRegister(name, email, password)
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.localizationRegister.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBarRegister.visibility = if(isLoading) View.VISIBLE else View.GONE
    }
}