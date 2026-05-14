package sfedu.ictis.walkOfInterest.data.mapper

import sfedu.ictis.walkOfInterest.data.model.dto.PoiInfoDto
import sfedu.ictis.walkOfInterest.data.model.dto.TagNameDto
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiTag
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.PoiStatus

fun PoiInfoDto.toDomain(): DomainPoiInfo = DomainPoiInfo(
    id = id,
    point = point?.let { DomainPoint(it.lat, it.lon) },
    name = name,
    description = description,
    tags = tags.orEmpty().mapNotNull { it.toDomainOrNull() },
    status = parsePoiStatus(status),
    rating = rating ?: 0.0,
    countRate = countRate ?: 0,
    photoUrl = photoUrl
)

private fun TagNameDto.toDomainOrNull(): DomainPoiTag? {
    val subcatId = tag?.subcategoryId ?: return null
    val subcatName = subcategoryName ?: return null
    return DomainPoiTag(
        subcategoryId = subcatId,
        subcategoryName = subcatName,
        categoryId = categoryId,
        weight = tag.weight
    )
}

private fun parsePoiStatus(raw: String?): PoiStatus = when (raw) {
    "APPROVED" -> PoiStatus.APPROVED
    "REJECTED" -> PoiStatus.REJECTED
    else -> PoiStatus.PENDING
}