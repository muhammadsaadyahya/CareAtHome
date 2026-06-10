package com.example.careathome

import DiscoverFragment
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.careathome.R
import com.example.careathome.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), addToBackStack = false)
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (!isCurrentFragment(HomeFragment::class.java)) {
                        loadFragment(HomeFragment())
                    }
                    true
                }
                R.id.nav_discover -> {
                    if (!isCurrentFragment(DiscoverFragment::class.java)) {
                        loadFragment(DiscoverFragment())
                    }
                    true
                }
                R.id.nav_chat -> {
                    if (!isCurrentFragment(Chats::class.java)) {
                        loadFragment(Chats())
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (!isCurrentFragment(Profile::class.java)) {
                        loadFragment(Profile())
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            if (addToBackStack) {
                addToBackStack(null)
            }
            commit()
        }
    }

    private fun isCurrentFragment(fragmentClass: Class<out Fragment>): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        return currentFragment != null && currentFragment.javaClass == fragmentClass
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }
}