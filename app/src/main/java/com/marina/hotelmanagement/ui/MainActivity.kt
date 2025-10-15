package com.marina.hotelmanagement.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.marina.hotelmanagement.HotelApplication
import com.marina.hotelmanagement.R
import com.marina.hotelmanagement.databinding.ActivityMainBinding
import com.marina.hotelmanagement.ui.viewmodel.factory.HotelViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModelFactory: HotelViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModelFactory with repository
        val repository = (application as HotelApplication).repository
            ?: throw IllegalStateException("Repository not initialized")
        viewModelFactory = HotelViewModelFactory(repository)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}