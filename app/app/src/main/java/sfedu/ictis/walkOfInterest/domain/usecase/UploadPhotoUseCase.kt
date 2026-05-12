package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.domain.repository.UserRepository

class UploadPhotoUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(bytes: ByteArray, extension: String): Result<DomainUserProfile> =
        repository.uploadPhoto(bytes, extension)
}
