package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository

class GetPoiByIdUseCase(private val repository: PoiRepository) {
    suspend operator fun invoke(id: Long, lang: String = "ru"): Result<DomainPoiInfo> =
        repository.getPoiById(id, lang)
}