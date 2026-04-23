package sfedu.ictis.walkOfInterest.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.data.local.TokenStorage
import sfedu.ictis.walkOfInterest.data.model.LoginRequest
import sfedu.ictis.walkOfInterest.data.model.RegisterRequest
import sfedu.ictis.walkOfInterest.domain.repository.AuthRepository


class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenStorage: TokenStorage
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

            val currentState = _uiState.value
            val result = if (currentState.mode == AuthMode.REGISTER) {
                if (pass != pass2) {
                    _uiState.update { it.copy(isLoading = false, error = "Пароли не совпадают") }
                    return@launch
                }
                repository.register(RegisterRequest(user, email, pass, first, second))
            } else {
                repository.login(LoginRequest(email, pass))
            }

            result.onSuccess { authResponse ->
                // TODO: Сохранить токены
                tokenStorage.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message ?: "Ошибка авторизации") }
            }
        }
    }
}