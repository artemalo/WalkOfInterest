package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.domain.repository.UserRepository

class GetMyProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<DomainUserProfile> = repository.getMyProfile()
}