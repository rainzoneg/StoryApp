package com.dicoding.storyapp.ui.register

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
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var registerViewModel: RegisterViewModel

    @Test
    fun `Register Failed`() = mainCoroutineRule.runBlockingTest {
        val response = MutableLiveData<PostResponse>()
        response.value = PostResponse(true, "Username is already taken")
        val actualResponse : PostResponse
        `when`(registerViewModel.registerResponse).thenReturn(response)
        registerViewModel.requestRegister("test", "test", "test")
        actualResponse = registerViewModel.registerResponse.getOrAwaitValue()
        Assert.assertTrue(actualResponse.error)
    }

    @Test
    fun `Register Succesful`(){
        val response = MutableLiveData<PostResponse>()
        response.value = PostResponse(false, "User created")
        val actualResponse : PostResponse
        `when`(registerViewModel.registerResponse).thenReturn(response)
        registerViewModel.requestRegister("test", "test", "test")
        actualResponse = registerViewModel.registerResponse.getOrAwaitValue()
        Assert.assertFalse(actualResponse.error)
    }
}