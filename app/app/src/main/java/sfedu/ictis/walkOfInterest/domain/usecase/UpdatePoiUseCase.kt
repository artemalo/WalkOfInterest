package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository

class UpdatePoiUseCase(
    private val repository: PoiRepository
) {
    suspend operator fun invoke(
        id: Long,
        point: DomainPoint,
        name: String,
        description: String?,
        lang: String,
        subcategoryIds: List<Int>
    ): Result<DomainPoiInfo> = repository.updatePoi(
        id = id,
        point = point,
        name = name,
        description = description,
        lang = lang,
        subcategoryIds = subcategoryIds
    )
}
