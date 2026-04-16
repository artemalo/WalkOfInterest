package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository

class CalculateWalkUseCase(private val repository: RouteRepository) {
    suspend operator fun invoke(from: DomainPoint, to: DomainPoint, time: Int): Result<List<DomainCategory>> {
        // Здесь можно добавить проверку: если время меньше минимального - вернуть ошибку сразу
        return repository.searchWalk(from, to, time)
    }
}