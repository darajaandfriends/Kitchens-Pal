package com.kitchenspal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kitchenspal.R
import com.kitchenspal.ui.fragment.AddPhotoFragment
import com.kitchenspal.ui.fragment.ArchiveFragment
import com.kitchenspal.ui.fragment.HomeFragment
import com.kitchenspal.ui.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Ganti dengan fragment yang ingin ditampilkan untuk Menu Item 1
                    val fragment = HomeFragment()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.addPhoto -> {
                    // Ganti dengan fragment yang ingin ditampilkan untuk Menu Item 2
                    val fragment = AddPhotoFragment()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.archive -> {
                    // Ganti dengan fragment yang ingin ditampilkan untuk Menu Item 3
                    val fragment = ArchiveFragment()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    // Ganti dengan fragment yang ingin ditampilkan untuk Menu Item 4
                    val fragment = ProfileFragment()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val defaultFragment = HomeFragment()
        loadFragment(defaultFragment)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

    }
}