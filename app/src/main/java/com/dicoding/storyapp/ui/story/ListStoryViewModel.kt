package com.dicoding.storyapp.ui.story

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.helper.SettingsPreferences
import com.dicoding.storyapp.helper.StoryRepository
import com.dicoding.storyapp.model.ListStoryItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ListStoryViewModel(private val storyRepository: StoryRepository, private val pref: SettingsPreferences) : ViewModel() {

    private var token: String

    init{
        runBlocking{
            token = pref.getSessionToken().first()
        }
    }

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory : LiveData<PagingData<ListStoryItem>> = storyRepository.getStory(token).cachedIn(viewModelScope)


    fun getSessionToken() : LiveData<String>{
        return pref.getSessionToken().asLiveData()
    }

    fun deleteSessionInfo(){
        viewModelScope.launch {
            pref.deleteSessionInfo()
        }
    }

    companion object{
        private const val TAG = "ListStoryViewModel"
    }
}