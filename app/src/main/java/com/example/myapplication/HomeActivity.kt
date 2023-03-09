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

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val playFragment = PlayFragment()
    private val recommendFragment = RecommendFragment()
    private val libraryFragment = LibraryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


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