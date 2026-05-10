package sfedu.ictis.walkOfInterest.domain.model

data class PoiOrder(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val order: Int = 0
)