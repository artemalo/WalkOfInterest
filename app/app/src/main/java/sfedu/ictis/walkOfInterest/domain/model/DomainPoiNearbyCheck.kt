package sfedu.ictis.walkOfInterest.domain.model

data class DomainPoiNearbyCheck(
    val existsNearby: Boolean,
    val pois: List<DomainPoiNearby>
)
