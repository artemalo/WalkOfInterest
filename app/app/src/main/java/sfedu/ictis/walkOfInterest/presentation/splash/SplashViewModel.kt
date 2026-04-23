package sfedu.ictis.walkOfInterest.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.data.local.TokenStorage

class SplashViewModel(
    private val tokenStorage: TokenStorage
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _destination = MutableSharedFlow<NextDestination>()
    val destination = _destination.asSharedFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            delay(300)

            val hasToken = tokenStorage.getRefreshToken() != null

            _isLoading.value = false

            if (hasToken) {
                _destination.emit(NextDestination.Main)
            } else {
                _destination.emit(NextDestination.Auth)
            }
        }
    }

    sealed class NextDestination {
        object Main : NextDestination()
        object Auth : NextDestination()
    }
}