package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository

class CreatePoiUseCase(
    private val repository: PoiRepository
) {
    suspend operator fun invoke(
        point: DomainPoint,
        name: String,
        description: String?,
        lang: String,
        subcategoryIds: List<Int>,
        force: Boolean
    ): Result<DomainPoiInfo> = repository.createPoi(
        point = point,
        name = name,
        description = description,
        lang = lang,
        subcategoryIds = subcategoryIds,
        force = force
    )
}
