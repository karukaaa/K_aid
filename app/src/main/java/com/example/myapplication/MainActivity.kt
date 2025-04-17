package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.Fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        val currentUser = auth.currentUser
        if (currentUser != null) {
            showBottomNavigation()
            loadFragment(HomeFragment())
        } else {
            hideBottomNavigation()
            loadLoginFragment()
        }
    }

    fun onLoginSuccess() {
        showBottomNavigation()
        clearBackStack()
        loadFragment(HomeFragment())
    }

    fun onLogout() {
        auth.signOut()
        hideBottomNavigation()
        clearBackStack()
        loadLoginFragment()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_requests -> {
                    loadFragment(RequestsListFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(AdministratorProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun showBottomNavigation() {
        bottomNavigationView.visibility = View.VISIBLE
        setupBottomNavigation()
    }

    private fun hideBottomNavigation() {
        bottomNavigationView.visibility = View.GONE
    }

    private fun loadLoginFragment() {
        bottomNavigationView.visibility = View.GONE  // Убедимся, что скрыт
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LogInFragment())
            .commit()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun clearBackStack() {
        supportFragmentManager.popBackStack(
            null,
            androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}
