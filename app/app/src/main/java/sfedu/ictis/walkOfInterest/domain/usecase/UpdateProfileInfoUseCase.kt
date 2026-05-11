package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.domain.repository.UserRepository
import sfedu.ictis.walkOfInterest.domain.util.AuthValidator

class UpdateProfileInfoUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(firstName: String, lastName: String, bio: String): Result<DomainUserProfile> {
        val trimFirstName = firstName.trim()
        val trimLastName = lastName.trim()
        val trimBio = bio.trim()


        if (trimFirstName.isBlank()) {
            return Result.failure(Exception("Имя не может быть пустым"))
        }
        if (trimLastName.isBlank()) {
            return Result.failure(Exception("Фамилия не может быть пустым"))
        }

        if (!AuthValidator.isValidName(trimFirstName) || !AuthValidator.isValidName(trimLastName)) {
            return Result.failure(Exception("Имя и фамилия должны содержать только буквы, до ${AuthValidator.MAX_FIELD_LENGTH} символов"))
        }

        if (!AuthValidator.isValidBio(trimBio)) {
            return Result.failure(Exception("Описание профиля слишком длинное (макс. ${AuthValidator.MAX_BIO_LENGTH} символов)"))
        }

        return repository.updateProfileInfo(trimFirstName, trimLastName, trimBio)
    }
}