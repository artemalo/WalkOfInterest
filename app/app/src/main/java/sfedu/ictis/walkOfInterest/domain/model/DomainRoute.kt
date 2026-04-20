package sfedu.ictis.walkOfInterest.domain.model

data class DomainRoute(
    val id: String = java.util.UUID.randomUUID().toString(),
    val minTime: Int,
    val distance: Double,
    val steps: Int,

    val points: List<DomainPoint>
)