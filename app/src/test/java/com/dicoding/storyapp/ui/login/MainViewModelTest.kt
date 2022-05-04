package com.dicoding.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.MainCoroutineRule
import com.dicoding.storyapp.getOrAwaitValue
import com.dicoding.storyapp.helper.SettingsPreferences
import com.dicoding.storyapp.model.LoginResponse
import com.dicoding.storyapp.model.LoginResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mainViewModel: MainViewModel
    @Mock
    private lateinit var pref: SettingsPreferences
    private lateinit var actualMainViewModel: MainViewModel

    @Before
    fun setUp(){
        actualMainViewModel = MainViewModel(pref)
    }

    @Test
    fun `Login Failed (Invalid Password)`() = mainCoroutineRule.runBlockingTest {
        val dummyParams : String = "testParams"
        actualMainViewModel.requestLogin("$dummyParams@gmail.com", "asdasd")
        val actualResponse : LoginResponse = actualMainViewModel.loginResponse.getOrAwaitValue()
        Assert.assertTrue(actualResponse.error)
        Assert.assertEquals("Invalid Password", actualResponse.message)
    }

    @Test
    fun `Login Succesful`(){
        val response = MutableLiveData<LoginResponse>()
        response.value = LoginResponse(LoginResult("test", "test", "test"),false, "success")
        val actualResponse : LoginResponse
        Mockito.`when`(mainViewModel.loginResponse).thenReturn(response)
        mainViewModel.requestLogin("test", "test")
        actualResponse = mainViewModel.loginResponse.getOrAwaitValue()
        Assert.assertFalse(actualResponse.error)
        Assert.assertEquals("success", actualResponse.message)
    }

    @Test
    fun `When Fetch Token Not Null`() = mainCoroutineRule.runBlockingTest {
        val expectedTokenFlow : Flow<String> = flow {
                val expectedToken = "thisIsMyTestingToken"
                emit(expectedToken)
        }
        Mockito.`when`(pref.getSessionInfo()).thenReturn(expectedTokenFlow)
        val actualToken = actualMainViewModel.getSessionInfo().getOrAwaitValue()
        Assert.assertNotNull(actualToken)
        Assert.assertEquals("thisIsMyTestingToken", actualToken)
    }

    @Test
    fun `When Save Session Success Call Pref saveSessionInfo`() = mainCoroutineRule.runBlockingTest {
        val dummyParams : String = "test"
        actualMainViewModel.saveSessionInfo(dummyParams, dummyParams)
        Mockito.verify(pref).saveSessionInfo(dummyParams, dummyParams)
    }
}