package sfedu.ictis.walkOfInterest.data.mapper

import sfedu.ictis.walkOfInterest.data.model.dto.PoiCardDto
import sfedu.ictis.walkOfInterest.data.model.dto.TagNameDto
import sfedu.ictis.walkOfInterest.domain.model.DomainMyPoi
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiTag
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.PoiStatus

fun PoiCardDto.toDomain(): DomainMyPoi = DomainMyPoi(
    id = id,
    point = point?.let { DomainPoint(it.lat, it.lon) },
    name = name,
    description = description,
    tags = tags.orEmpty().mapNotNull { it.toMyPoiTagOrNull() },
    status = parseStatus(status),
    rejectionReason = rejectionReason?.takeIf { it.isNotBlank() },
    rating = rating ?: 0.0,
    countRate = countRate ?: 0
)

private fun TagNameDto.toMyPoiTagOrNull(): DomainPoiTag? {
    val subcatId = tag?.subcategoryId ?: return null
    val subcatName = subcategoryName ?: return null
    return DomainPoiTag(
        subcategoryId = subcatId,
        subcategoryName = subcatName,
        categoryId = categoryId,
        weight = tag.weight
    )
}

private fun parseStatus(raw: String?): PoiStatus = when (raw) {
    "APPROVED" -> PoiStatus.APPROVED
    "REJECTED" -> PoiStatus.REJECTED
    else -> PoiStatus.PENDING
}
