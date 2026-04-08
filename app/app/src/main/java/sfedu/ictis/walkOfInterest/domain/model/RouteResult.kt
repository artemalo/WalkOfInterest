package sfedu.ictis.walkOfInterest.domain.model

data class RouteResult(
    val minTime: Int,
    val distance: Double,
    val points: List<DomainPoint>
)