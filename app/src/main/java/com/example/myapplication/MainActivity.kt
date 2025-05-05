package com.example.myapplication

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.profileauth.ProfileFragment
import com.example.myapplication.profileauth.LogInFragment
import com.example.myapplication.requestlist.RequestsListFragment
import com.example.myapplication.requestcreation.AppDatabase
import com.example.myapplication.requestcreation.NetworkChangeReceiver
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val auth = FirebaseAuth.getInstance()

    private lateinit var networkReceiver: NetworkChangeReceiver


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

        networkReceiver = NetworkChangeReceiver {
            // Call sync logic here
            lifecycleScope.launch {
                syncLocalRequestsToFirestore()
            }
        }

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
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
                    loadFragment(ProfileFragment())
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

    suspend fun syncLocalRequestsToFirestore() {
        val db = AppDatabase.getDatabase(applicationContext)
        val localRequests = db.requestDao().getAllRequests()

        val firestore = FirebaseFirestore.getInstance()
        localRequests.forEach { request ->
            firestore.collection("requests")
                .add(request)
                .addOnSuccessListener {
                    lifecycleScope.launch {
                        db.requestDao().deleteRequest(request)
                    }
                }
                .addOnFailureListener {
                    // Retry later
                }
        }
    }

}
