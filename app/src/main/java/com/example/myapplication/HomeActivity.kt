package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myapplication.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val playFragment = PlayFragment()
    private val recommendFragment = RecommendFragment()
    private val libraryFragment = LibraryFragment()
    private val profileFragment = ProfileFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val database = FirebaseDatabase.getInstance().reference
        val email = intent.getStringExtra("EMAIL") ?: ""
        Log.i("pref","email got???${email}")
        //Get Preferences from database for recommendation logic
        //R from CRUD
        val query: Query = database.child("emails").orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val emailNode = dataSnapshot.children.first()
                    val preferencesNode = emailNode.child("preferences")
                    val brightPref = preferencesNode.child("BRIGHT").value.toString()
                    val mediumPref = preferencesNode.child("MEDIUM").value.toString()
                    val darkPref = preferencesNode.child("DARK").value.toString()
                    // Do something with the preferences values
                    Log.i("pref",brightPref+mediumPref+darkPref)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })



        //getEmail(token)
       /* email->dark->rap, hip-hop
             ->light->classical
             ->medium->pop, piano*/
        /*Open up recommendation forms (fragments)*/
        bottomNavigationView=findViewById(R.id.bottom_navigationBar)


        supportFragmentManager.beginTransaction().replace(R.id.container, playFragment).commit()

        bottomNavigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.play -> {
                        supportFragmentManager.beginTransaction().replace(R.id.container, playFragment).commit()
                        return true
                    }
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

    override fun onStart() {
        super.onStart()
        Log.i("myTag","OnStart for Home Activity has been called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("myTag","OnResume for Home Activity has been called")
    }

    override fun onPause() {
        super.onPause()
        Log.i("myTag","OnPause for Home Activity has been called")
    }

    override fun onStop() {
        super.onStop()
        Log.i("myTag","OnStop for Home Activity has been called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("myTag","OnDestroy for Home Activity has been called")
    }
}