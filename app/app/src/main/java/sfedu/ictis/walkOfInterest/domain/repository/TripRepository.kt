package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainTrip

interface TripRepository {
    suspend fun saveCurrentTrip(trip: DomainTrip)
    suspend fun getCurrentTrip(): DomainTrip?
    suspend fun getAllTrips(): List<DomainTrip>
    suspend fun getTripById(id: String): DomainTrip?
    suspend fun delTripById(id: String): Boolean
    suspend fun updateBestRouteTime(tripId: String, time: Int)
}