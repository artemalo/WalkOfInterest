package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository

class UpsertMyReviewUseCase(private val repository: PoiRepository) {

    suspend operator fun invoke(
        poiId: Long,
        rating: Int,
        content: String?,
        lang: String = "ru"
    ): Result<DomainReview> {
        if (poiId <= 0) {
            return Result.failure(IllegalArgumentException("Некорректный POI"))
        }
        if (rating !in 1..5) {
            return Result.failure(IllegalArgumentException("Оценка должна быть от 1 до 5"))
        }

        val trimmed = content?.trim().orEmpty()
        if (trimmed.length > MAX_CONTENT_LENGTH) {
            return Result.failure(
                IllegalArgumentException("Текст отзыва слишком длинный (макс. $MAX_CONTENT_LENGTH символов)")
            )
        }

        return repository.upsertMyReview(
            poiId = poiId,
            rating = rating,
            content = trimmed.ifBlank { null },
            lang = lang
        )
    }

    companion object {
        const val MAX_CONTENT_LENGTH = 1024
    }
}