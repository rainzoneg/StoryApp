package com.dicoding.storyapp.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.MainCoroutineRule
import com.dicoding.storyapp.getOrAwaitValue
import com.dicoding.storyapp.model.ListStoryItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapStoryViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mapStoryViewModel: MapStoryViewModel

    @Test
    fun `When Fetch Token Not Null`() = mainCoroutineRule.runBlockingTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = "testString"
        Mockito.`when`(mapStoryViewModel.getSessionToken()).thenReturn(expectedToken)
        val actualToken = mapStoryViewModel.getSessionToken().getOrAwaitValue()
        Assert.assertNotNull(actualToken)
        Assert.assertEquals(actualToken, expectedToken.value)
    }

    @Test
    fun `When Get Story Succesful and Not Null`() = mainCoroutineRule.runBlockingTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val story = MutableLiveData<List<ListStoryItem>>()
        story.value = dummyStory
        Mockito.`when`(mapStoryViewModel.listStory).thenReturn(story)
        val actualStory : List<ListStoryItem> = mapStoryViewModel.listStory.getOrAwaitValue()
        Mockito.verify(mapStoryViewModel).listStory
        Assert.assertNotNull(actualStory)
        Assert.assertEquals(dummyStory.size, actualStory.size)
        Assert.assertEquals(dummyStory[0].name, actualStory[0].name)
    }
}