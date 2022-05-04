package com.dicoding.storyapp.ui.login

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.storyapp.R
import com.dicoding.storyapp.helper.EspressoIdlingResource
import com.dicoding.storyapp.ui.add.AddStoryActivity
import com.dicoding.storyapp.ui.add.CameraActivity
import com.dicoding.storyapp.ui.detail.StoryDetailActivity
import com.dicoding.storyapp.ui.map.MapStoryActivity
import com.dicoding.storyapp.ui.story.ListStoryActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainTest{

    @get:Rule
    val activity = ActivityScenarioRule(ListStoryActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
    @Test
    fun checkListAndDetail(){
        Intents.init()
        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
        onView(withId(R.id.maps)).check(matches(isDisplayed()))
        onView(withId(R.id.add)).check(matches(isDisplayed()))
        onView(withId(R.id.localization)).check(matches(isDisplayed()))
        onView(withId(R.id.logout)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_story)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(10))
        onView(withId(R.id.rv_story)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
        onView(withId(R.id.rv_story)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        intended(hasComponent(StoryDetailActivity::class.java.name))
        onView(withId(R.id.tv_detail_user)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
        Intents.release()
    }
    @Test
    fun loadStoryAddTryCamera(){
        Intents.init()
        onView(withId(R.id.add)).perform(click())
        intended(hasComponent(AddStoryActivity::class.java.name))
        onView(withId(R.id.btn_cameraX)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_cameraX)).perform(click())
        intended(hasComponent(CameraActivity::class.java.name))
        onView(withId(R.id.viewFinder)).check(matches(isDisplayed()))
        onView(withId(R.id.captureImage)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.btn_submitStory)).check(matches(isDisplayed()))
        Intents.release()
    }
    @Test
    fun loadStoryTrySubmit(){
        Intents.init()
        onView(withId(R.id.add)).perform(click())
        intended(hasComponent(AddStoryActivity::class.java.name))
        onView(withId(R.id.btn_cameraX)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_cameraX)).perform(click())
        intended(hasComponent(CameraActivity::class.java.name))
        onView(withId(R.id.viewFinder)).check(matches(isDisplayed()))
        onView(withId(R.id.captureImage)).check(matches(isDisplayed()))
        onView(withId(R.id.captureImage)).perform(click())
        Intents.release()
    }
    @Test
    fun loadStoryMap() {
        Intents.init()
        onView(withId(R.id.maps)).perform(click())
        intended(hasComponent(MapStoryActivity::class.java.name))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
        Intents.release()
    }
}