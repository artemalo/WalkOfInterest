package sfedu.ictis.walkOfInterest.domain.model

data class RouteFromToResult(
    val minTime: Int,
    val distance: Double,
    val points: List<DomainPoint>
)