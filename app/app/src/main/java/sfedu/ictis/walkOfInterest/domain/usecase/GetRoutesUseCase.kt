package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository

class GetRoutesUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(trip: DomainTrip): Result<List<DomainRoute>> {
        if (trip.selectedPois.isEmpty()) {
            return Result.failure(Exception("Список выбранных точек пуст"))
        }

        val requestPoints = buildList {
            add(trip.from)
            addAll(trip.selectedPois.map { DomainPoint(it.lat, it.lon) })
            add(trip.to)
        }

        return repository.getRoutes(requestPoints)
    }
}