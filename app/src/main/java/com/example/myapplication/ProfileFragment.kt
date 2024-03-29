package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
private lateinit var mivProfilePic: ImageView
private lateinit var mtvUsername: TextView
private lateinit var mtvEmail: TextView
private lateinit var mtvFollowerCount: TextView
class ProfileFragment : Fragment() {
    private lateinit var email: String

    data class UserProfile(
        val displayName: String,
        val email: String,
        val followers: Int,
        val imageUrl: String?
    )
    fun setEmail(emailFromHome: String) {
        email = emailFromHome
        Log.i("frag", email)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mtvUsername = view.findViewById<TextView>(R.id.username)
        mtvEmail = view.findViewById<TextView>(R.id.email)
        mtvFollowerCount = view.findViewById<TextView>(R.id.followerCount)
        mivProfilePic = view.findViewById(R.id.profilePic)
        val accessToken = arguments?.getString("accessToken")

        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            var userData: UserProfile? = fetchUserProfile(accessToken)
            Picasso.get().load(userData?.imageUrl).into(mivProfilePic) // load album image using Picasso library
            mtvUsername.setText("Username: " + userData?.displayName)
            mtvEmail.setText("Email: " + userData?.email)
            mtvFollowerCount.setText("Followers: " + userData?.followers.toString())
        }
        // Here you can access your views and add your logic
        val deleteAccButt = view.findViewById<Button>(R.id.deleteAccButton)
        val database = FirebaseDatabase.getInstance().reference
        //If user presses delete account button on profile page
        //The database removes all data about the user
        //D from CRUD
        deleteAccButt.setOnClickListener {
            Log.i("delete", "delete account button clicked")
            val query: Query = database.child("emails").orderByChild("email").equalTo(email)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User exists in the database
                        val userNode = dataSnapshot.children.first()
                        // Delete the user node using the DatabaseReference object
                        userNode.ref.removeValue()
                        // Show success message
                        Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
                        val loginIntent= Intent(requireContext(), LoginActivity::class.java)
                        startActivity(loginIntent)
                    } else {
                        // User doesn't exist in the database
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Log.e("FIREBASE", "Error deleting user: ${error.message}")
                    Toast.makeText(context, "Error deleting user: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    suspend fun fetchUserProfile(accessToken: String?): UserProfile? = withContext(Dispatchers.IO) {
        val url = URL("https://api.spotify.com/v1/me")
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer $accessToken")

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            val displayName = jsonResponse.optString("display_name")
            val email = jsonResponse.optString("email")
            val followers = jsonResponse.getJSONObject("followers").optInt("total")
            val imageUrl = jsonResponse.getJSONArray("images").optJSONObject(0)?.optString("url")

            return@withContext UserProfile(displayName, email, followers, imageUrl)
        } else {
            Log.e("FetchUserProfile", "HTTP error code: $responseCode")
            return@withContext null
        }
    }

}