package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainSavedPoi
import sfedu.ictis.walkOfInterest.domain.repository.SavedPoiRepository

class GetSavedPoisUseCase(private val repository: SavedPoiRepository) {
    suspend operator fun invoke(): List<DomainSavedPoi> = repository.getAll()
}
