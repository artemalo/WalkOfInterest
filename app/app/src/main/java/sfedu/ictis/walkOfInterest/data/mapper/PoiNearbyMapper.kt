package sfedu.ictis.walkOfInterest.data.mapper

import sfedu.ictis.walkOfInterest.data.model.dto.PoiNearbyCheckResponseDto
import sfedu.ictis.walkOfInterest.data.model.dto.PoiNearbyDto
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiNearby
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiNearbyCheck
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint

fun PoiNearbyCheckResponseDto.toDomain(): DomainPoiNearbyCheck = DomainPoiNearbyCheck(
    existsNearby = existsNearby,
    pois = pois.orEmpty()
        .groupBy { it.id }
        .values
        .map { rows -> rows.toAggregatedDomain() }
)

private fun List<PoiNearbyDto>.toAggregatedDomain(): DomainPoiNearby {
    val first = first()
    val categoryNames = mapNotNull { it.categoryName?.takeIf { n -> n.isNotBlank() } }
        .distinct()
    val subcategoryNames = mapNotNull { it.subcategoryName?.takeIf { n -> n.isNotBlank() } }
        .distinct()
    return DomainPoiNearby(
        id = first.id,
        name = first.name?.takeIf { it.isNotBlank() } ?: "Без названия",
        categoryName = categoryNames.joinToString(" • ").takeIf { it.isNotBlank() },
        subcategoryName = subcategoryNames.joinToString(", ").takeIf { it.isNotBlank() },
        point = first.point?.let { DomainPoint(it.lat, it.lon) },
        distanceMeters = first.distanceMeters
    )
}
