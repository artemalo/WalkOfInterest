package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.repository.TripRepository

class GetCurrentTripUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(): DomainTrip? {
        return tripRepository.getCurrentTrip()
    }
}