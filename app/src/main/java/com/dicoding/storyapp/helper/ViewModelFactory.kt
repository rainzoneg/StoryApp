package com.dicoding.storyapp.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.ui.add.AddStoryViewModel
import com.dicoding.storyapp.ui.login.MainViewModel
import com.dicoding.storyapp.ui.map.MapStoryViewModel
import com.dicoding.storyapp.ui.story.ListStoryViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory private constructor(private val application: Application,private val pref: SettingsPreferences) : ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ListStoryViewModel::class.java)){
            return ListStoryViewModel(Injection.provideRepository(application), pref) as T
        }else if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(pref) as T
        }else if(modelClass.isAssignableFrom(AddStoryViewModel::class.java)){
            return AddStoryViewModel(pref) as T
        }else if(modelClass.isAssignableFrom(MapStoryViewModel::class.java)){
            return MapStoryViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object{
        @Volatile
        private var INSTANCE : ViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application, pref: SettingsPreferences) : ViewModelFactory{
            if(INSTANCE == null){
                synchronized(ViewModelFactory::class.java){
                    INSTANCE = ViewModelFactory(application, pref)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}