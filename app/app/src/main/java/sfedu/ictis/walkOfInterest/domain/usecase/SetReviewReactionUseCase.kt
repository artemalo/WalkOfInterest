package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.ReactionType
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository
import sfedu.ictis.walkOfInterest.domain.repository.ReviewReactionState

class SetReviewReactionUseCase(
    private val repository: PoiRepository
) {
    suspend operator fun invoke(
        reviewId: Long,
        type: ReactionType
    ): Result<ReviewReactionState> {
        if (reviewId <= 0) {
            return Result.failure(IllegalArgumentException("Некорректный идентификатор отзыва"))
        }
        return repository.setReviewReaction(reviewId, type)
    }
}