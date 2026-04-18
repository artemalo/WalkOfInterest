package sfedu.ictis.walkOfInterest.domain.model

data class DomainRoute(
    val id: Int,
    val timeMinutes: Int,
    val stepsCount: Int,
    val pois: List<RoutePoint>
)