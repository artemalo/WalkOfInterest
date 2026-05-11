package sfedu.ictis.walkOfInterest.domain.model

data class DomainSavedPoi(
    val poiId: Long,
    val name: String,
    val address: String,
    val savedAt: Long
)
