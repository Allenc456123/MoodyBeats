package com.example.myapplication

import android.content.Context
import android.widget.CheckBox
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test

class JUnitTest {
    @Test
    fun testParsePrefs() {
        // Arrange
        val context: Context = ApplicationProvider.getApplicationContext()
        val checkbox1 = CheckBox(context)
        val checkbox2 = CheckBox(context)
        val checkbox3 = CheckBox(context)
        val checkBoxList1 = mutableListOf(checkbox1, checkbox2, checkbox3)
        val checkBoxList2 = mutableListOf(checkbox1, checkbox2, checkbox3)
        val checkBoxList3 = mutableListOf(checkbox1, checkbox2, checkbox3)

        checkbox1.isChecked = true
        checkbox2.isChecked = false
        checkbox3.isChecked = true
        checkbox1.text = "pop"
        checkbox2.text = "rock"
        checkbox3.text = "country"

        val rec = RecommendationActivity()
        rec.checkBoxList1 = checkBoxList1
        rec.checkBoxList2 = checkBoxList2
        rec.checkBoxList3 = checkBoxList3

        // Act
        val result = rec.parsePrefs()

        // Assert
        assertEquals(result[0], "pop,country")
        assertEquals(result[1], "pop,country")
        assertEquals(result[2], "pop,country")
    }

}
