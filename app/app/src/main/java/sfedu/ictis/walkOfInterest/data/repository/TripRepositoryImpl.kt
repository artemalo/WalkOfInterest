package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.repository.TripRepository

class TripRepositoryImpl : TripRepository {
    private var currentTrip: DomainTrip? = null
    private val tripsList = mutableListOf<DomainTrip>() // TODO DB


    override fun saveCurrentTrip(trip: DomainTrip) {
        currentTrip = trip

        if (!tripsList.any { it.id == trip.id }) {
            tripsList.add(trip)
        }
    }

    override fun getCurrentTrip(): DomainTrip? = currentTrip

    override fun getAllTrips(): List<DomainTrip> = tripsList.toList()
}