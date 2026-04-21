package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.data.local.TripDao
import sfedu.ictis.walkOfInterest.data.mapper.toDomain
import sfedu.ictis.walkOfInterest.data.mapper.toEntity
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.repository.TripRepository

class TripRepositoryImpl(
    private val tripDao: TripDao
) : TripRepository {
    private var currentTrip: DomainTrip? = null

    override suspend fun saveCurrentTrip(trip: DomainTrip) {
        currentTrip = trip
        tripDao.insertTrip(trip.toEntity())
    }

    override suspend fun getCurrentTrip(): DomainTrip? = currentTrip

    override suspend fun getAllTrips(): List<DomainTrip> {
        return tripDao.getAllTrips().map { it.toDomain() }
    }

    override suspend fun getTripById(id: String): DomainTrip? {
        return tripDao.getTripById(id)?.toDomain()
    }

    override suspend fun delTripById(id: String): Boolean   {
        return tripDao.deleteTripById(id) > 0
    }
}