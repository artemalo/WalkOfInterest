package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.data.model.AuthResponse
import sfedu.ictis.walkOfInterest.data.model.LoginRequest
import sfedu.ictis.walkOfInterest.domain.repository.AuthRepository
import sfedu.ictis.walkOfInterest.domain.util.AuthValidator

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String): Result<AuthResponse> {
        if (!AuthValidator.isNoneBlank(email, pass)) {
            return Result.failure(Exception("Все поля должны быть заполнены"))
        }

        if (!AuthValidator.isValidEmail(email)) {
            return Result.failure(Exception("Некорректный формат почты"))
        }

        if (!AuthValidator.isValidPassword(pass)) {
            return Result.failure(Exception("Пароль должен содержать минимум 8 символов"))
        }

        return repository.login(LoginRequest(email, pass))
    }
}