package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.repository.SavedPoiRepository

class IsPoiSavedUseCase(private val repository: SavedPoiRepository) {
    suspend operator fun invoke(poiId: Long): Boolean = repository.isSaved(poiId)
}
