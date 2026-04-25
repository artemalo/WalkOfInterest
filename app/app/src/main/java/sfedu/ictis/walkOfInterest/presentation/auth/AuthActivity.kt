package sfedu.ictis.walkOfInterest.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.ActivityAuthBinding
import sfedu.ictis.walkOfInterest.presentation.main.MainActivity


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeState()
        observeEvent()
    }

    private fun setupListeners() {
        binding.textIsAlready.setOnClickListener {
            viewModel.toggleAuthMode()
        }

        binding.fieldBtnAuth.setOnClickListener {
            viewModel.submit(
                email = binding.email.text.toString(),
                pass = binding.password1.text.toString(),
                pass2 = binding.password2.text.toString(),
                user = binding.username.text.toString(),
                first = binding.firstName.text.toString(),
                second = binding.secondName.text.toString()
            )
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUiMode(state.mode)

                    binding.fieldBtnAuth.isEnabled = !state.isLoading
                    binding.btnAuth.isEnabled = !state.isLoading

                    binding.textError.text = state.error ?: ""
                    binding.textError.visibility = if (state.error != null) View.VISIBLE else View.GONE


                    if (state.isSuccess) {
                        binding.btnAuthText.text = getString(R.string.success)

                        val intent = Intent(this@AuthActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun observeEvent() { // TODO: TESTING!!!
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEvent.collect {
                    val intent = Intent(this@AuthActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun updateUiMode(mode: AuthMode) {
        val isRegister = mode == AuthMode.REGISTER

        binding.firstName.parent.let { (it as View).visibility = if (isRegister) View.VISIBLE else View.GONE }
        binding.secondName.parent.let { (it as View).visibility = if (isRegister) View.VISIBLE else View.GONE }
        binding.username.parent.let { (it as View).visibility = if (isRegister) View.VISIBLE else View.GONE }
        binding.password2.parent.let { (it as View).visibility = if (isRegister) View.VISIBLE else View.GONE }


        if (isRegister) {
            binding.textAuth.text = getString(R.string.Register)
            binding.btnAuthText.text = getString(R.string.auth_text_register)

            binding.textIsAlready.text = HtmlCompat.fromHtml(getString(R.string.auth_combined), HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else {
            binding.textAuth.text = getString(R.string.SignIn)
            binding.btnAuthText.text = getString(R.string.auth_text_login)
            binding.textIsAlready.text = HtmlCompat.fromHtml(getString(R.string.register_combined), HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }
}