package sfedu.ictis.walkOfInterest.domain.model

data class DomainTrip(
    val id: String,
    val addressFrom: String,
    val addressTo: String,

    val from: DomainPoint,
    val to: DomainPoint,

    val bestRouteTime: Int?,
    val userSelectedTime: Int,

    val totalPois: Int,
    val selectedPois: List<RoutePoint>,

    val photo: String?
)