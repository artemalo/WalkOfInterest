package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.data.local.SavedPoiDao
import sfedu.ictis.walkOfInterest.data.local.SavedPoiEntity
import sfedu.ictis.walkOfInterest.domain.model.DomainSavedPoi
import sfedu.ictis.walkOfInterest.domain.repository.SavedPoiRepository

class SavedPoiRepositoryImpl(private val dao: SavedPoiDao) : SavedPoiRepository {

    override suspend fun getAll(): List<DomainSavedPoi> =
        dao.getAll().map { it.toDomain() }

    override suspend fun isSaved(poiId: Long): Boolean =
        dao.exists(poiId) > 0

    override suspend fun save(poi: DomainSavedPoi) =
        dao.insert(poi.toEntity())

    override suspend fun remove(poiId: Long) =
        dao.delete(poiId)
}

private fun SavedPoiEntity.toDomain() = DomainSavedPoi(
    poiId = poiId,
    name = name,
    address = address,
    savedAt = savedAt,
    photo = photo
)

private fun DomainSavedPoi.toEntity() = SavedPoiEntity(
    poiId = poiId,
    name = name,
    address = address,
    savedAt = savedAt,
    photo = photo
)
