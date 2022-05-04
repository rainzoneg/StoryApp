package com.dicoding.storyapp.helper

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dicoding.storyapp.database.StoryDatabase
import com.dicoding.storyapp.model.ListStoryItem

class StoryRepository (private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(token, storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}