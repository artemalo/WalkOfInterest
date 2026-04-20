package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository

class GenerateRoutesUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(trip: DomainTrip): Result<List<DomainRoute>> {
        if (trip.selectedPois.isEmpty()) {
            return Result.failure(Exception("Список выбранных точек пуст"))
        }

        val requestPoints = trip.selectedPois.map { DomainPoint(it.lat, it.lon) }

        return repository.getRoutes(requestPoints)
    }
}