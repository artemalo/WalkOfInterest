package sfedu.ictis.walkOfInterest.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.usecase.LoginUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.RegisterUseCase


class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun toggleAuthMode() {
        _uiState.update { state ->
            val newMode = if (state.mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN
            state.copy(mode = newMode, error = null)
        }
    }

    fun submit(email: String, pass: String, pass2: String = "", user: String = "", first: String = "", second: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = if (_uiState.value.mode == AuthMode.REGISTER) {
                registerUseCase(email, pass, pass2, user, first, second)
            } else {
                loginUseCase(email, pass)
            }

            result.onSuccess { authResponse ->
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message ?: "Ошибка авторизации") }
            }
        }
    }
}