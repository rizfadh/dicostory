package com.rizfadh.dicostory

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.rizfadh.dicostory.ui.addstory.AddStoryActivity
import com.rizfadh.dicostory.ui.addstory.CameraActivity
import com.rizfadh.dicostory.ui.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddStoryTest {
    private val userToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWJIRTZ5MlpnT2xJcDY5dm0iLCJpYXQiOjE2ODQzMDA4MjR9.Mqvm96BMSl5QOoilS3pp7ForPEogTQW1QPWLuHw2XMs"
    private val addStoryIntent =
        Intent(ApplicationProvider.getApplicationContext(), AddStoryActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_TOKEN, userToken)
        }

    @get:Rule
    val activity: ActivityScenarioRule<AddStoryActivity> = ActivityScenarioRule(addStoryIntent)

    @Test
    fun addStorySuccess() {
        onView(withId(R.id.ed_add_description)).check(matches(isDisplayed()))
            .perform(typeText("T"), closeSoftKeyboard())

        Intents.init()
        onView(withId(R.id.btn_add_camera)).perform(click())
        Intents.intended(hasComponent(CameraActivity::class.java.name))
        onView(withId(R.id.btn_take_picture)).check(matches(isDisplayed())).perform(click())

        Thread.sleep(1000)
        onView(withId(R.id.button_add)).check(matches(isDisplayed())).perform(click())
        Thread.sleep(3000)
        onView(withText(R.string.upload_success)).check(matches(isDisplayed()))
    }
}