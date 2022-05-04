package com.dicoding.storyapp.helper

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.model.ListStoryItem

class StoryPagingSource(private val token: String, private val apiService : ApiService) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let{ anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        wrapEspressoIdlingResource{
            return try{
                val position = params.key ?: INITIAL_PAGE_INDEX
                val client = apiService.getPagedStories(
                    "Bearer $token",
                    position,
                    params.loadSize)
                LoadResult.Page(
                    data = client.listStory,
                    prevKey = if(position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if(client.listStory.isNullOrEmpty()) null else position + 1
                )
            } catch(e: Exception){
                return LoadResult.Error(e)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}