package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainSavedPoi

interface SavedPoiRepository {
    suspend fun getAll(): List<DomainSavedPoi>
    suspend fun isSaved(poiId: Long): Boolean
    suspend fun save(poi: DomainSavedPoi)
    suspend fun remove(poiId: Long)
}
