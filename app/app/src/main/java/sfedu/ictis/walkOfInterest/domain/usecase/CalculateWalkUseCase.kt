package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository

class CalculateWalkUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(
        from: DomainPoint,
        to: DomainPoint,
        time: Int,
        maxPoi: Int
    ): Result<List<DomainCategory>> {
        return repository.searchWalk(from, to, time, maxPoi = maxPoi)
    }
}