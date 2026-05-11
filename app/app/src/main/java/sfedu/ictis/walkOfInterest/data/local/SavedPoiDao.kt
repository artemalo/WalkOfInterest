package sfedu.ictis.walkOfInterest.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedPoiDao {
    @Query("SELECT * FROM saved_pois ORDER BY savedAt DESC")
    suspend fun getAll(): List<SavedPoiEntity>

    @Query("SELECT COUNT(*) FROM saved_pois WHERE poiId = :poiId")
    suspend fun exists(poiId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SavedPoiEntity)

    @Query("DELETE FROM saved_pois WHERE poiId = :poiId")
    suspend fun delete(poiId: Long)
}
