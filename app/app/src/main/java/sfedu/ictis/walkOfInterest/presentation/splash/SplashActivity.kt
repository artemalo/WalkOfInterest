package sfedu.ictis.walkOfInterest.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.presentation.auth.AuthActivity
import sfedu.ictis.walkOfInterest.presentation.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.destination.collect { dest ->
                    navigateToNext(dest)
                    finish()
                }
            }
        }
    }

    private fun navigateToNext(destination: SplashViewModel.NextDestination) {
        val targetClass = when (destination) {
            is SplashViewModel.NextDestination.Main -> MainActivity::class.java
            is SplashViewModel.NextDestination.Auth -> AuthActivity::class.java
        }

        startActivity(Intent(this, targetClass))
    }
}