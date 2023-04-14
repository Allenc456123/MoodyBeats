package com.example.myapplication

import android.content.Context
import android.widget.CheckBox
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JUnitTest {
    @Test
    fun testParsePrefsSamePrefAllThree() {
        // Arrange
        val scenario = ActivityScenario.launch(RecommendationActivity::class.java)
        scenario.onActivity { activity ->
            val checkBoxList1 = mutableListOf<CheckBox>()
            val checkBoxList2 = mutableListOf<CheckBox>()
            val checkBoxList3 = mutableListOf<CheckBox>()
            for (i in 1..10) {
                checkBoxList1.add(CheckBox(activity))
                checkBoxList2.add(CheckBox(activity))
                checkBoxList3.add(CheckBox(activity))
            }

            checkBoxList1[0].isChecked = true
            checkBoxList1[0].text = "Pop"
            checkBoxList1[1].isChecked = true
            checkBoxList1[1].text = "Rock"
            checkBoxList2[0].isChecked = true
            checkBoxList2[0].text = "Pop"
            checkBoxList2[1].isChecked = true
            checkBoxList2[1].text = "Rock"
            checkBoxList3[0].isChecked = true
            checkBoxList3[0].text = "Pop"
            checkBoxList3[1].isChecked = true
            checkBoxList3[1].text = "Rock"


            activity.checkBoxList1 = checkBoxList1
            activity.checkBoxList2 = checkBoxList2
            activity.checkBoxList3 = checkBoxList3

            // Act
            val result = activity.parsePrefs()

            // Assert
            assertEquals(listOf("pop,rock", "pop,rock", "pop,rock"), result)
        }
    }
    @Test
    fun testParsePrefsDiffPrefAllThree() {
        // Arrange
        val scenario = ActivityScenario.launch(RecommendationActivity::class.java)
        scenario.onActivity { activity ->
            val checkBoxList1 = mutableListOf<CheckBox>()
            val checkBoxList2 = mutableListOf<CheckBox>()
            val checkBoxList3 = mutableListOf<CheckBox>()
            for (i in 1..10) {
                checkBoxList1.add(CheckBox(activity))
                checkBoxList2.add(CheckBox(activity))
                checkBoxList3.add(CheckBox(activity))
            }


            // set checkbox values
            checkBoxList1[0].isChecked = true
            checkBoxList1[0].text = "Pop"
            checkBoxList1[1].isChecked = true
            checkBoxList1[1].text = "Rock"
            checkBoxList2[0].isChecked = true
            checkBoxList2[0].text = "Hip-Hop"
            checkBoxList2[1].isChecked = true
            checkBoxList2[1].text = "EDM"
            checkBoxList3[0].isChecked = true
            checkBoxList3[0].text = "Country"
            checkBoxList3[1].isChecked = true
            checkBoxList3[1].text = "Jazz"
            checkBoxList3[2].isChecked = true
            checkBoxList3[2].text = "Classical"


            activity.checkBoxList1 = checkBoxList1
            activity.checkBoxList2 = checkBoxList2
            activity.checkBoxList3 = checkBoxList3

            // Act
            val result = activity.parsePrefs()

            // Assert
            assertEquals(listOf("pop,rock", "hip-hop,edm", "country,jazz,classical"), result)
        }
    }
    @Test
    fun testParsePrefsAllSelectedAllThree() {
        // Arrange
        val scenario = ActivityScenario.launch(RecommendationActivity::class.java)
        scenario.onActivity { activity ->
            val checkBoxList1 = mutableListOf<CheckBox>()
            val checkBoxList2 = mutableListOf<CheckBox>()
            val checkBoxList3 = mutableListOf<CheckBox>()
            val genres = arrayOf("Rock", "Pop", "Jazz", "Classical", "Country", "Alt-rock", "Hip-Hop", "EDM", "R-n-B", "Indie")
            for (i in 0 until 10) {
                val checkBox1 = CheckBox(activity)
                checkBox1.isChecked = true
                checkBoxList1.add(checkBox1)

                val checkBox2 = CheckBox(activity)
                checkBox2.isChecked = true
                checkBoxList2.add(checkBox2)

                val checkBox3 = CheckBox(activity)
                checkBox3.isChecked = true
                checkBoxList3.add(checkBox3)

                checkBoxList1[i].text = genres[i]
                checkBoxList2[i].text = genres[i]
                checkBoxList3[i].text = genres[i]
            }
            activity.checkBoxList1 = checkBoxList1
            activity.checkBoxList2 = checkBoxList2
            activity.checkBoxList3 = checkBoxList3

            // Act
            val result = activity.parsePrefs()

            // Assert
            assertEquals(result[0], "rock,pop,jazz,classical,country,alt-rock,hip-hop,edm,r-n-b,indie")
            assertEquals(result[1], "rock,pop,jazz,classical,country,alt-rock,hip-hop,edm,r-n-b,indie")
            assertEquals(result[2], "rock,pop,jazz,classical,country,alt-rock,hip-hop,edm,r-n-b,indie")
        }
    }
}
