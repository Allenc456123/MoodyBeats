package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
        Log.i("MYTAG", "HomeActivity : OnCreate")
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

}