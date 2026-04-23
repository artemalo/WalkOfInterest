package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.data.model.AuthResponse
import sfedu.ictis.walkOfInterest.data.model.RegisterRequest
import sfedu.ictis.walkOfInterest.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String, pass: String, pass2: String,
        user: String, first: String, second: String
    ): Result<AuthResponse> {
        if (pass != pass2) {
            return Result.failure(Exception("Пароли не совпадают"))
        }

        val request = RegisterRequest(user, email, pass, first, second)
        return repository.register(request)
    }
}