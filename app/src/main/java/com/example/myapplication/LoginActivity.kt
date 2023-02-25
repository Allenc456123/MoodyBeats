package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

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