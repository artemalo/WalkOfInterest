package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.data.model.AuthResponse
import sfedu.ictis.walkOfInterest.data.model.LoginRequest
import sfedu.ictis.walkOfInterest.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String): Result<AuthResponse> {
        return repository.login(LoginRequest(email, pass))
    }
}