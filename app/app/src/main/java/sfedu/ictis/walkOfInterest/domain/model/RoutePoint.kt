package sfedu.ictis.walkOfInterest.domain.model

data class RoutePoint(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val categoryId: Int,

    val name: String,
    val nameCat: String,
    val nameSubcat: String
)