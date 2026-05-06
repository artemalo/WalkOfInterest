package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainMyPoi
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository

class GetMyPoisUseCase(
    private val repository: PoiRepository
) {
    suspend operator fun invoke(lang: String = "ru"): Result<List<DomainMyPoi>> =
        repository.getMyPois(lang)
}
