package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainTrip

interface TripRepository {
    fun saveCurrentTrip(trip: DomainTrip)
    fun getCurrentTrip(): DomainTrip?
    fun getAllTrips(): List<DomainTrip>
}