package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository

class SupplementPoiUseCase(
    private val repository: PoiRepository
) {
    suspend operator fun invoke(
        id: Long,
        name: String,
        description: String?,
        lang: String,
        subcategoryIds: List<Int>
    ): Result<DomainPoiInfo> = repository.supplementPoi(
        id = id,
        name = name,
        description = description,
        lang = lang,
        subcategoryIds = subcategoryIds
    )
}
