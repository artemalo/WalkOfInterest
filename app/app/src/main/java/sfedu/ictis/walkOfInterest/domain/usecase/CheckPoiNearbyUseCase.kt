package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiNearbyCheck
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository

class CheckPoiNearbyUseCase(
    private val repository: PoiRepository
) {
    suspend operator fun invoke(
        point: DomainPoint,
        lang: String = "ru"
    ): Result<DomainPoiNearbyCheck> = repository.checkNearby(point, lang)
}
