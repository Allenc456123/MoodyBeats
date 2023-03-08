package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox

class RecommendationActivity : AppCompatActivity() {

    private lateinit var checkBoxList1: MutableList<CheckBox>
    private lateinit var checkBoxList2: MutableList<CheckBox>
    private lateinit var checkBoxList3: MutableList<CheckBox>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)
        val token = intent.getStringExtra("TOKEN_KEY")

        // Find all the checkboxes by their IDs and add them to separate lists
        checkBoxList1 = mutableListOf(
            findViewById(R.id.checkbox1_1),
            findViewById(R.id.checkbox1_2),
            findViewById(R.id.checkbox1_3),
            findViewById(R.id.checkbox1_4),
            findViewById(R.id.checkbox1_5),
            findViewById(R.id.checkbox1_6),
            findViewById(R.id.checkbox1_7),
            findViewById(R.id.checkbox1_8),
            findViewById(R.id.checkbox1_9),
            findViewById(R.id.checkbox1_10)
        )

        checkBoxList2 = mutableListOf(
            findViewById(R.id.checkbox2_1),
            findViewById(R.id.checkbox2_2),
            findViewById(R.id.checkbox2_3),
            findViewById(R.id.checkbox2_4),
            findViewById(R.id.checkbox2_5),
            findViewById(R.id.checkbox2_6),
            findViewById(R.id.checkbox2_7),
            findViewById(R.id.checkbox2_8),
            findViewById(R.id.checkbox2_9),
            findViewById(R.id.checkbox2_10)
        )

        checkBoxList3 = mutableListOf(
            findViewById(R.id.checkbox3_1),
            findViewById(R.id.checkbox3_2),
            findViewById(R.id.checkbox3_3),
            findViewById(R.id.checkbox3_4),
            findViewById(R.id.checkbox3_5),
            findViewById(R.id.checkbox3_6),
            findViewById(R.id.checkbox3_7),
            findViewById(R.id.checkbox3_8),
            findViewById(R.id.checkbox3_9),
            findViewById(R.id.checkbox3_10)
        )

        // Set a listener for when any of the checkboxes are clicked
        val checkboxClickListener = View.OnClickListener { view ->
            if (view is CheckBox) {
                val isChecked = view.isChecked

                // Find which list the checkbox belongs to and update the list accordingly
                if (checkBoxList1.any { it.id == view.id }) {
                    checkBoxList1.forEach { it.isChecked = isChecked }
                } else if (checkBoxList2.any { it.id == view.id }) {
                    checkBoxList2.forEach { it.isChecked = isChecked }
                } else if (checkBoxList3.any { it.id == view.id }) {
                    checkBoxList3.forEach { it.isChecked = isChecked }
                }
            }
        }


        // Set the listener for all the checkboxes
        checkBoxList1.forEach { it.setOnClickListener(checkboxClickListener) }
        checkBoxList2.forEach { it.setOnClickListener(checkboxClickListener) }
        checkBoxList3.forEach { it.setOnClickListener(checkboxClickListener) }
    }

    /*fun getSelectedCheckboxes(): List<String> {
        // Return a list of strings for the text of the selected checkboxes
        return listOf(
            checkBoxList1.filter { it.isChecked }.map { it.text.toString() },
            checkBoxList2.filter { it.isChecked }.map { it.text.toString() },
            checkBoxList3.filter { it.isChecked }.map { it.text.toString() }
        )
    }
     */
}
