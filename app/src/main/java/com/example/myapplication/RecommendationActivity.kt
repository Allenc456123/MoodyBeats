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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.HashMap

class RecommendationActivity : AppCompatActivity() {

    private lateinit var checkBoxList1: MutableList<CheckBox>
    private lateinit var checkBoxList2: MutableList<CheckBox>
    private lateinit var checkBoxList3: MutableList<CheckBox>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)
        val token = intent.getStringExtra("TOKEN_KEY")
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
            val preferences: List<String> = parsePrefs()
            GlobalScope.launch(Dispatchers.Main) {
                Log.i("SubmitButton", "token"+"$token")
                val email = getUserEmail(token)
                Log.i("SubmitButton", "Submit button clicked; User email: $email")
                // Get a reference to the Firebase Realtime Database
                val database = FirebaseDatabase.getInstance().reference
                // Check if the email already exists in the database
                //If email already exists then update the existing preferences with new
                //U from CRUD
                val query: Query = database.child("emails").orderByChild("email").equalTo(email)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        intent.putExtra("EMAIL", email)
                        intent.putExtra("TOKEN", token)
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
                            val childUpdates =
                                hashMapOf<String, Any>("/emails/$emailKey/preferences" to prefValues)
                            database.updateChildren(childUpdates)
                        } else {
                            // Email doesn't exist in the database, create a new node and save "preferences"
                            val key: String = database.child("emails").push().key ?: ""
                            val emailNode = database.child("emails").child(key)
                            emailNode.child("email").setValue(email)
                            emailNode.child("preferences").child("BRIGHT").setValue(preferences[0])
                            emailNode.child("preferences").child("MEDIUM").setValue(preferences[1])
                            emailNode.child("preferences").child("DARK").setValue(preferences[2])

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
                Log.i("SubmitButton", "Submit button clicked; User email: $email")
            }
        }

    }

    private fun parsePrefs(): List<String> {
        var brightStr: String = ""
        var mediumStr: String = ""
        var darkStr: String = ""
        //Log.i("MY_TAG", checkBoxList1[0].isChecked.toString())
        for(i in 0..9){
            if(checkBoxList1[i].isChecked){
                brightStr = brightStr.plus(checkBoxList1[i].text.toString().lowercase()).plus(",")
            }
            if(checkBoxList2[i].isChecked){
                mediumStr = mediumStr.plus(checkBoxList2[i].text.toString().lowercase()).plus(",")
            }
            if(checkBoxList3[i].isChecked){
                darkStr = darkStr.plus(checkBoxList3[i].text.toString().lowercase()).plus(",")
            }
        }

        /*
        Returns list where:
            ret[0] -> Bright Prefs
            ret[1] -> Medium Prefs
            ret[2] -> Dark Prefs
         */
        return listOf<String>(brightStr.substring(0,brightStr.length-1),mediumStr.substring(0,mediumStr.length-1),darkStr.substring(0,darkStr.length-1))
    }

    suspend fun getUserEmail(accessToken: String?): String? = withContext(Dispatchers.IO) {
        val url = URL("https://api.spotify.com/v1/me")
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer $accessToken")

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            val email = jsonResponse.getString("email")

            return@withContext email
        } else {
            Log.e("GetUserEmail", "HTTP error code: $responseCode")
            return@withContext null
        }
    }

}
