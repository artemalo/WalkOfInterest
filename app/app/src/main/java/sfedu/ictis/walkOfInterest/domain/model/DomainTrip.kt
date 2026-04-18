package sfedu.ictis.walkOfInterest.domain.model

data class DomainTrip(
    val id: String, // UUID.randomUUID().toString()
    val addressFrom: String,
    val addressTo: String,

    val from: DomainPoint,
    val to: DomainPoint,

    val totalTime: Int,
    val totalPois: Int,

    val selectedPois: List<RoutePoint>
)