package com.dicoding.storyapp.ui.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.MainCoroutineRule
import com.dicoding.storyapp.getOrAwaitValue
import com.dicoding.storyapp.model.PostResponse
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
class AddStoryViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var addStoryViewModel: AddStoryViewModel

    @Test
    fun `When Fetch Token Not Null`() = mainCoroutineRule.runBlockingTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = "testString"
        Mockito.`when`(addStoryViewModel.getSessionToken()).thenReturn(expectedToken)
        val actualToken = addStoryViewModel.getSessionToken().getOrAwaitValue()
        Assert.assertNotNull(actualToken)
        Assert.assertEquals(actualToken, expectedToken.value)
    }

    @Test
    fun `When Upload Story Succesful`() = mainCoroutineRule.runBlockingTest {
        val response = MutableLiveData<PostResponse>()
        response.value = PostResponse(false, "Upload successful.")
        val actualResponse : PostResponse
        Mockito.`when`(addStoryViewModel.uploadResponse).thenReturn(response)
        actualResponse = addStoryViewModel.uploadResponse.getOrAwaitValue()
        Assert.assertFalse(actualResponse.error)
    }

    @Test
    fun `When Upload Story Failed`() = mainCoroutineRule.runBlockingTest {
        val response = MutableLiveData<PostResponse>()
        response.value = PostResponse(true, "Upload failed try again.")
        val actualResponse : PostResponse
        Mockito.`when`(addStoryViewModel.uploadResponse).thenReturn(response)
        actualResponse = addStoryViewModel.uploadResponse.getOrAwaitValue()
        Assert.assertTrue(actualResponse.error)
    }
}