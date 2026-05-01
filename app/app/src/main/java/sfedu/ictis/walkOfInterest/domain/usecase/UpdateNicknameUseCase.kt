package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.domain.repository.UserRepository
import sfedu.ictis.walkOfInterest.domain.util.AuthValidator

class UpdateNicknameUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(newUsername: String): Result<DomainUserProfile> {
        val trimmed = newUsername.trim()
        if (trimmed.isBlank()) {
            return Result.failure(Exception("Никнейм не может быть пустым"))
        }
        if (!AuthValidator.isValidUsername(trimmed)) {
            return Result.failure(Exception("Логин некорректен (только буквы, цифры и _, до 50 символов)"))
        }
        return repository.updateNickname(trimmed)
    }
}