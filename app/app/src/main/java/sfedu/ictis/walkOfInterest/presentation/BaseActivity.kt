package sfedu.ictis.walkOfInterest.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import sfedu.ictis.walkOfInterest.presentation.auth.AuthActivity
import sfedu.ictis.walkOfInterest.utils.SessionManager

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    protected val TAG = "Lifecycle_${this::class.java.simpleName}"

    protected val sessionManager: SessionManager by inject()

    protected lateinit var binding: VB
    abstract fun inflateBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        binding = inflateBinding()
        setContentView(binding.root)

        observeSession()
    }

    private fun observeSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessionManager.logoutEvent.collect {
                    Log.d(TAG, "logoutEvent received -> navigateToAuth")
                    navigateToAuth()
                }
            }
        }
    }

    protected fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}