package sfedu.ictis.walkOfInterest.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import sfedu.ictis.walkOfInterest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // только NavHost + BottomNav
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host)

        binding.bottomNav.setupWithNavController(navController)
    }
}