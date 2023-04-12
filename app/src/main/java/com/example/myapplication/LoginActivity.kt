package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

private const val CLIENT_ID = "8a9c4c8a356e484eb01e57b844806133"
private const val REDIRECT_URI = "com.example.myapplication://callback"
private const val REQUEST_CODE = 1337


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf("streaming, user-read-email, playlist-read-private, " +
                "playlist-read-collaborative, " +
                "playlist-modify-private, " +
                "playlist-modify-public"))
        val request = builder.build()
        setContentView(R.layout.activity_login)

        //Log.i("myTag","OnCreate for Login Activity has been called")

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, data)

            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    val token = response.accessToken
                    //Log.d("AUTHENTICATION", "Got Spotify access token: $token")
                    // Save token to SharedPreferences or use it to make API requests
                    val intent = Intent(this, RecommendationActivity::class.java)
                    intent.putExtra("TOKEN_KEY", token)
                    startActivity(intent)
                }
                AuthorizationResponse.Type.ERROR -> {
                    //Log.e("AUTHENTICATION", "Failed to authenticate with Spotify: ${response.error}")
                    // Handle authentication error
                }
                else -> {
                    //Log.e("AUTHENTICATION", "Unknown authentication response type: ${response.type}")
                    // Handle unknown response type
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //Log.i("myTag","OnResume for Login Activity has been called")
    }

    override fun onPause() {
        super.onPause()
        //Log.i("myTag","OnPause for Login Activity has been called")
    }

    override fun onStop() {
        super.onStop()
        //Log.i("myTag","OnStop for Login Activity has been called")
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.i("myTag","OnDestroy for Login Activity has been called")
    }
}