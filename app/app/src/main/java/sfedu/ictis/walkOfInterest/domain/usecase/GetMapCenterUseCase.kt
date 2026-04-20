package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.repository.MapSettingRepository

class GetMapCenterUseCase(private val mapRepository: MapSettingRepository) {
    operator fun invoke(): DomainPoint {
        return mapRepository.getMapCenter()
    }
}