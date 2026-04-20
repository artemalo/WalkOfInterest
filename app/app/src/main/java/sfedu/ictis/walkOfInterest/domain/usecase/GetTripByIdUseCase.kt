package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.repository.TripRepository

class GetTripByIdUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(id: String): DomainTrip? {
        return tripRepository.getTripById(id)
    }
}