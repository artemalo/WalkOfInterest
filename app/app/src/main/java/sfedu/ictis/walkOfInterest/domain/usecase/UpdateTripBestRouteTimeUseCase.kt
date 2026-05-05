package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.repository.TripRepository

class UpdateTripBestRouteTimeUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(tripId: String, time: Int) {
        tripRepository.updateBestRouteTime(tripId, time)
    }
}