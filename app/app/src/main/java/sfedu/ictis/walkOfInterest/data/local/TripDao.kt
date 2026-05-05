package sfedu.ictis.walkOfInterest.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    suspend fun getAllTrips(): List<TripEntity>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    suspend fun getTripById(tripId: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: String): Int

    @Query("UPDATE trips SET bestRouteTime = :time WHERE id = :tripId")
    suspend fun updateBestRouteTime(tripId: String, time: Int): Int
}