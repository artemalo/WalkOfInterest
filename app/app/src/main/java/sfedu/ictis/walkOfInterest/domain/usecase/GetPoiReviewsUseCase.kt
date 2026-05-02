package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository

class GetPoiReviewsUseCase(private val repository: PoiRepository) {
    suspend operator fun invoke(id: Long, lang: String = "ru"): Result<List<DomainReview>> =
        repository.getReviewsByPoiId(id, lang)
}