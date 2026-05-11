package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.domain.repository.UserRepository

class GetProfileByUsernameUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String): Result<DomainUserProfile> =
        repository.getProfileByUsername(username)
}
