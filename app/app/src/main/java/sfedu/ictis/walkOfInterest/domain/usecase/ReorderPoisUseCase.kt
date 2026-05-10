package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.PoiOrder
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository

class ReorderPoisUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(
        pois: List<PoiOrder>,
        from: DomainPoint,
        to: DomainPoint
    ): Result<List<PoiOrder>> = repository.reorder(pois, from, to)
}