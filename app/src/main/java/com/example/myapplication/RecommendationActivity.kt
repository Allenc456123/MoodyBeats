package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.*

class RecommendationActivity : AppCompatActivity() {

    private lateinit var checkBoxList1: MutableList<CheckBox>
    private lateinit var checkBoxList2: MutableList<CheckBox>
    private lateinit var checkBoxList3: MutableList<CheckBox>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)
        val token = intent.getStringExtra("TOKEN_KEY")

        val queue = Volley.newRequestQueue(this)
        val url = "https://api.spotify.com/v1/me"
        var email=""
        val intent = Intent(this, HomeActivity::class.java)




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



        val submitButton = findViewById<Button>(R.id.submit_button)

        submitButton.setOnClickListener {
            val preferences : List<String> = parsePrefs()
            val request = object : JsonObjectRequest(
                Method.GET, url, null,
                Response.Listener { response ->
                    email = response.getString("email")
                    // Get a reference to the Firebase Realtime Database
                    val database = FirebaseDatabase.getInstance().reference

                    // Check if the email already exists in the database
                    val query: Query = database.child("emails").orderByChild("email").equalTo(email)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            intent.putExtra("EMAIL", email)
                            startActivity(intent)
                            if (dataSnapshot.exists()) {
                                // Email already exists in the database
                                Log.i("FIREBASE", "Email already exists")
                                // Get the key of the existing email node
                                val emailNode = dataSnapshot.children.first()
                                val emailKey = emailNode.key ?: ""
                                // Save the "preferences" string for the existing email node
                                val prefValues = hashMapOf<String, Any>(
                                    "BRIGHT" to preferences[0],
                                    "MEDIUM" to preferences[1],
                                    "DARK" to preferences[2]
                                )
                                val childUpdates = hashMapOf<String, Any>("/emails/$emailKey/preferences" to prefValues)
                                database.updateChildren(childUpdates)
                            } else {
                                // Email doesn't exist in the database, create a new node and save "preferences"
                                val key: String = database.child("emails").push().key ?: ""
//                                val emailValues = hashMapOf<String, Any>(
//                                    "email" to email
//                                )
                                val emailNode = database.child("emails").child(key)
                                emailNode.child("email").setValue(email)
                                emailNode.child("preferences").child("BRIGHT").setValue(preferences[0])
                                emailNode.child("preferences").child("MEDIUM").setValue(preferences[1])
                                emailNode.child("preferences").child("DARK").setValue(preferences[2])

//                                val childUpdates = hashMapOf<String, Any>("/emails/$key" to emailValues)
//                                database.updateChildren(childUpdates)

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
                    Log.i("SubmitButton", "Submit button clicked; User email: $email")
                },
                Response.ErrorListener { error ->
                    Log.e("SPOTIFY", "Error getting user email: ${error.message}")
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }
            }
            queue.add(request)

        }
    }

    private fun parsePrefs(): List<String> {
        var brightStr: String = ""
        var mediumStr: String = ""
        var darkStr: String = ""
        //Log.i("MY_TAG", checkBoxList1[0].isChecked.toString())
        for(i in 0..9){
            if(checkBoxList1[i].isChecked){
                brightStr = brightStr.plus(checkBoxList1[i].text.toString()).plus(".")
            }
            if(checkBoxList2[i].isChecked){
                mediumStr = mediumStr.plus(checkBoxList2[i].text.toString()).plus(".")
            }
            if(checkBoxList3[i].isChecked){
                darkStr = darkStr.plus(checkBoxList3[i].text.toString()).plus(".")
            }
        }

        /*
        Returns list where:
            ret[0] -> Bright Prefs
            ret[1] -> Medium Prefs
            ret[2] -> Dark Prefs
         */
        return listOf<String>(brightStr,mediumStr,darkStr)
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
