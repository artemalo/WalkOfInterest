package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository

class GetCategoryTimeUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(
        from: DomainPoint,
        to: DomainPoint,
        category: DomainCategory
    ): Result<Int> {
        val selectedPoints = category.subcategories
            .flatMap { it.pois }
            .filter { it.selected }
            .map { DomainPoint(it.lat, it.lon) }

        if (selectedPoints.isEmpty()) {
            return Result.success(0)
        }

        val points = buildList {
            add(from)
            addAll(selectedPoints)
            add(to)
        }

        return repository.getTime(points)
    }
}