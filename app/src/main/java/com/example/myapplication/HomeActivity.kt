package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.database.*
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val recommendFragment = RecommendFragment()
    private val libraryFragment = LibraryFragment()
    private val profileFragment = ProfileFragment()
    private lateinit var spotifyAppRemote: SpotifyAppRemote
    private lateinit var playlistNames: Map<String,String>
    private lateinit var userID: String
    private lateinit var brightPref: String
    private lateinit var mediumPref: String
    private lateinit var darkPref: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val database = FirebaseDatabase.getInstance().reference
        val email = intent.getStringExtra("EMAIL") ?: ""
        Log.i("pref","email got???${email}")
        val accessToken  = intent.getStringExtra("TOKEN") // retrieve the access token for the user
        Log.i("token", "token got???${accessToken}");
        val bundle = Bundle()
        bundle.putString("accessToken", accessToken)
        recommendFragment.arguments = bundle
        libraryFragment.arguments= bundle
        profileFragment.arguments = bundle

        val targetNames = listOf("MoodyBeats-Dark", "MoodyBeats-Bright", "MoodyBeats-Medium")

        lifecycleScope.launch {
            playlistNames = getPlaylistNames(accessToken)

            userID = getUserId(accessToken)
            targetNames.forEach { targetName ->
                if (!playlistNames.contains(targetName)) {
                    //Log.i("myTag","createPlaylist is called for ${targetName}")
                    createPlaylist(accessToken, userID, targetName)

                } else {
                    //Log.i("myTag","${targetName} already exists")
                }
            }
            playlistNames = getPlaylistNames(accessToken)
            bundle.putString("darkID",playlistNames.get("MoodyBeats-Dark"));
            bundle.putString("brightID",playlistNames.get("MoodyBeats-Bright"));
            bundle.putString("mediumID",playlistNames.get("MoodyBeats-Medium"));
        }

        //Get Preferences from database for recommendation logic
        //R from CRUD
        val query: Query = database.child("emails").orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val emailNode = dataSnapshot.children.first()
                    val preferencesNode = emailNode.child("preferences")
                    brightPref = preferencesNode.child("BRIGHT").value.toString()
                    mediumPref = preferencesNode.child("MEDIUM").value.toString()
                    darkPref = preferencesNode.child("DARK").value.toString()
                    // Do something with the preferences values
                    bundle.putString("bright", brightPref)
                    bundle.putString("medium", mediumPref)
                    bundle.putString("dark", darkPref)
                    //Log.i("pref",brightPref+mediumPref+darkPref)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        bottomNavigationView=findViewById(R.id.bottom_navigationBar)


        supportFragmentManager.beginTransaction().replace(R.id.container, libraryFragment).commit()

        bottomNavigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {

                    R.id.recommend -> {
                        supportFragmentManager.beginTransaction().replace(R.id.container, recommendFragment).commit()
                        return true
                    }
                    R.id.library -> {
                        supportFragmentManager.beginTransaction().replace(R.id.container, libraryFragment).commit()
                        return true
                    }
                    R.id.profile -> {
                        profileFragment.setEmail(email)
                        supportFragmentManager.beginTransaction().replace(R.id.container, profileFragment).commit()
                        return true
                    }
                }

                return false
            }
        })
    }


    //Returns map of form <Key = PlaylistName, Value = PlaylistID>
    suspend fun getPlaylistNames(accessToken: String?): Map<String, String> = withContext(Dispatchers.IO) {
        //Log.i("myTag","getPlayListName has been entered")
        val playlistUrl = "https://api.spotify.com/v1/me/playlists"
        val url = URL(playlistUrl)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer $accessToken")

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            val playlists = jsonResponse.getJSONArray("items")
            val playlistMap = mutableMapOf<String, String>()

            for (i in 0 until playlists.length()) {
                val playlist = playlists.getJSONObject(i)
                val playlistName = playlist.getString("name")
                val playlistID = playlist.getString("id")
                playlistMap[playlistName] = playlistID
            }

            //Log.i("myTag","getPlayListNamesMap is returning")
            return@withContext playlistMap
        } else {
            //Log.e("GetPlaylistNamesMap", "HTTP error code: $responseCode")
            //Log.i("myTag","getPlayListNamesMap is returning")
            return@withContext emptyMap()
        }
    }

    suspend fun createPlaylist(accessToken: String?, userId: String, playlistName: String) = withContext(Dispatchers.IO) {
        //Log.i("myTag", "createPlaylist: accessToken is ${accessToken}")
        //Log.i("myTag", "createPlaylist: userId is ${userId}")
        //Log.i("myTag", "createPlaylist: playlistName is ${playlistName}")
        val url = URL("https://api.spotify.com/v1/users/$userId/playlists")
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "Bearer $accessToken")
        connection.setRequestProperty("Content-Type", "application/json")

        val postData = JSONObject()
        postData.put("name", playlistName)
        postData.put("description", "Created by the MoodyBeats app")
        val requestBody = postData.toString().toByteArray(Charsets.UTF_8)

        connection.doOutput = true
        connection.outputStream.write(requestBody)

        val responseCode = connection.responseCode
        if (responseCode != HttpsURLConnection.HTTP_CREATED) {
            //Log.e("CreatePlaylist", "HTTP error code: $responseCode")
            //Log.i("myTag", "Response body: ${connection.responseCode.}")
            throw RuntimeException("Failed to create playlist")

        }
    }

    suspend fun getUserId(accessToken: String?): String = withContext(Dispatchers.IO) {
        val url = URL("https://api.spotify.com/v1/me")
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer $accessToken")

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            return@withContext jsonResponse.getString("id")
        } else {
            //.e("GetUserId", "HTTP error code: $responseCode")
            throw RuntimeException("Failed to get user ID")
        }
    }

    override fun onStart() {
        super.onStart()
        //Log.i("myTag","OnStart for Home Activity has been called")
    }

    override fun onResume() {
        super.onResume()
        //Log.i("myTag","OnResume for Home Activity has been called")
    }

    override fun onPause() {
        super.onPause()
        //Log.i("myTag","OnPause for Home Activity has been called")
    }

    override fun onStop() {
        super.onStop()
        //Log.i("myTag","OnStop for Home Activity has been called")
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.i("myTag","OnDestroy for Home Activity has been called")
    }
}