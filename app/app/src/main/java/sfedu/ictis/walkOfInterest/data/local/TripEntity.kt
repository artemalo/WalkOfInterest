package sfedu.ictis.walkOfInterest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.RoutePoint

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val addressFrom: String,
    val addressTo: String,

    val from: DomainPoint,
    val to: DomainPoint,

    val bestRouteTime: Int?,
    val userSelectedTime: Int,

    val totalPois: Int,
    val selectedPois: List<RoutePoint>,

    val photo: String? = null
)