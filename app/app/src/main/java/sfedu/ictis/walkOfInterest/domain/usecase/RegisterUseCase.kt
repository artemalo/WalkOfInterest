package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.data.model.AuthResponse
import sfedu.ictis.walkOfInterest.data.model.RegisterRequest
import sfedu.ictis.walkOfInterest.domain.repository.AuthRepository
import sfedu.ictis.walkOfInterest.domain.util.AuthValidator

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String, pass: String, pass2: String,
        user: String, first: String, second: String
    ): Result<AuthResponse> {
        if (!AuthValidator.isNoneBlank(email, user, first, second, pass, pass2)) {
            return Result.failure(Exception("Все поля должны быть заполнены"))
        }

        if (!AuthValidator.isValidUsername(user)) {
            return Result.failure(Exception("Логин некорректен (только буквы и цифры, до 50 символов)"))
        }

        if (!AuthValidator.isValidName(first) || !AuthValidator.isValidName(second)) {
            return Result.failure(Exception("Имя и фамилия должны содержать только буквы"))
        }

        if (!AuthValidator.isValidEmail(email)) {
            return Result.failure(Exception("Некорректный формат почты"))
        }

        if (!AuthValidator.isValidPassword(pass) || !AuthValidator.isValidPassword(pass2)) {
            return Result.failure(Exception("Пароль от 8 до 64 символов, буквы и цифры"))
        }

        if (pass != pass2) {
            return Result.failure(Exception("Пароли не совпадают"))
        }

        val request = RegisterRequest(user, email, pass, first, second)
        return repository.register(request)
    }
}