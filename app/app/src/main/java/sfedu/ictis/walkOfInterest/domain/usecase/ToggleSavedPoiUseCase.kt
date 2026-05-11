package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainSavedPoi
import sfedu.ictis.walkOfInterest.domain.repository.SavedPoiRepository

class ToggleSavedPoiUseCase(private val repository: SavedPoiRepository) {
    suspend operator fun invoke(poi: DomainSavedPoi): Boolean {
        return if (repository.isSaved(poi.poiId)) {
            repository.remove(poi.poiId)
            false
        } else {
            repository.save(poi)
            true
        }
    }
}
