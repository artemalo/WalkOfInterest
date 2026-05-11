package sfedu.ictis.walkOfInterest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_pois")
data class SavedPoiEntity(
    @PrimaryKey val poiId: Long,
    val name: String,
    val address: String,
    val savedAt: Long
)
