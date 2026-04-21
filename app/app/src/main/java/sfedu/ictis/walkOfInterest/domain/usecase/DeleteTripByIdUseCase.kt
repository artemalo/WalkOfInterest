package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.repository.TripRepository

class DeleteTripByIdUseCase(private val tripRepository: TripRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return tripRepository.delTripById(id)
    }
}