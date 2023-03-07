package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote


private const val CLIENT_ID = "8a9c4c8a356e484eb01e57b844806133"
private const val REDIRECT_URI = "http://example.com/callback/"
private var mSpotifyAppRemote: SpotifyAppRemote? = null

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.i("myTag","OnCreate for Login Activity has been called")

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val button = findViewById<Button>(R.id.signUpButton)

        // Set a click listener for the button
        button.setOnClickListener {
            // Create an Intent to open a browser
            val intent = Intent(Intent.ACTION_VIEW)

            // Set the data for the Intent (the URL to open)
            intent.data = Uri.parse("https://www.spotify.com/us/signup")

            // Start the Intent
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(this, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                    Log.d("MainActivity", "Connected! Yay!")

                    // Now you can start interacting with App Remote
                    //connected()
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("MainActivity", throwable.message, throwable)

                    // Something went wrong when attempting to connect! Handle errors here
                }
            })

        Log.i("myTag","OnStart for Login Activity has been called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("myTag","OnResume for Login Activity has been called")
    }

    override fun onPause() {
        super.onPause()
        Log.i("myTag","OnPause for Login Activity has been called")
    }

    override fun onStop() {
        super.onStop()
        Log.i("myTag","OnStop for Login Activity has been called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("myTag","OnDestroy for Login Activity has been called")
    }
}