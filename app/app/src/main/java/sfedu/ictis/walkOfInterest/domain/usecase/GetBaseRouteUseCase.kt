package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.RouteFromToResult
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository

class GetBaseRouteUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(from: DomainPoint, to: DomainPoint): Result<RouteFromToResult> {
        return repository.getRoute(from, to)
    }
}