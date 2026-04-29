package sfedu.ictis.walkOfInterest.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.usecase.LoginUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.RegisterUseCase
import sfedu.ictis.walkOfInterest.utils.SessionManager


class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent.asSharedFlow()

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
                sessionManager.resetLogout()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                _navigationEvent.emit(Unit)
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is retrofit2.HttpException -> {
                        when (exception.code()) {
                            401 -> "Неверные данные"
                            409 -> "Пользователь с такой почтой или ником уже существует"
                            400 -> "Ошибка в данных. Проверьте их точность"
                            else -> "Ошибка сервера: ${exception.code()}"
                        }
                    }
                    is java.io.IOException -> "Проверьте соединение с интернетом"
                    else -> exception.message ?: "Неизвестная ошибка"
                }

                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }
}