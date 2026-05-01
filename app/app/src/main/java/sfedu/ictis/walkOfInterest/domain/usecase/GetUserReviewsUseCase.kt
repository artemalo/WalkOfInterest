package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.repository.UserRepository

class GetUserReviewsUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String): Result<List<DomainReview>> {
        if (username.isBlank()) return Result.success(emptyList())
        return repository.getReviewsByUsername(username)
    }
}