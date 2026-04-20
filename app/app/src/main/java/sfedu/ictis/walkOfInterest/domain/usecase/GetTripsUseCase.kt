package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.repository.TripRepository

class GetTripsUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(): List<DomainTrip> {
        return tripRepository.getAllTrips()
    }
}