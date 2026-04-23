package sfedu.ictis.walkOfInterest.presentation.auth

enum class AuthMode { LOGIN, REGISTER }

data class AuthUiState(
    val mode: AuthMode = AuthMode.REGISTER,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)