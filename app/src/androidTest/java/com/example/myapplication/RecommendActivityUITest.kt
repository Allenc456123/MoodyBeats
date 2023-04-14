package com.example.myapplication

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecommendActivityUITest {
    @Test
    fun testCheckBox1() {
        // Click on recommend button
        val scenario = ActivityScenario.launch(RecommendationActivity::class.java)
        // Click on the first checkbox of the first checkbox list
        onView(withId(R.id.checkbox1_1))
            .perform(click())

        // Verify that the first checkbox of the first checkbox list is checked
        onView(withId(R.id.checkbox1_1))
            .check(matches(ViewMatchers.isChecked()))
    }
    @Test
    fun testCheckBox2() {
        // Click on recommend button
        val scenario = ActivityScenario.launch(RecommendationActivity::class.java)
        // Click on the first checkbox of the first checkbox list
        onView(withId(R.id.checkbox1_1))
            .perform(click())
        onView(withId(R.id.checkbox1_2))
            .perform(click())
        onView(withId(R.id.checkbox2_1))
            .perform(click())
        onView(withId(R.id.checkbox2_2))
            .perform(click())
        onView(withId(R.id.checkbox3_1))
            .perform(scrollTo(),click())
        onView(withId(R.id.checkbox3_2))
            .perform(scrollTo(),click())

        // Verify that the first checkbox of the first checkbox list is checked
        onView(withId(R.id.checkbox1_1))
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.checkbox1_2))
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.checkbox2_1))
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.checkbox2_2))
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.checkbox3_1))
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.checkbox3_2))
            .check(matches(ViewMatchers.isChecked()))
    }

    @Test
    fun testCheckBox3() {
        // Click on recommend button
        val scenario = ActivityScenario.launch(RecommendationActivity::class.java)
        // Click on the first checkbox of the first checkbox list
        onView(withId(R.id.checkbox3_10))
            .perform(scrollTo(), click())
        onView(withId(R.id.checkbox1_1))
            .perform(scrollTo(), click())

        // Verify that the first checkbox of the first checkbox list is checked
        onView(withId(R.id.checkbox3_10))
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.checkbox1_1))
            .check(matches(ViewMatchers.isChecked()))
    }
}
