package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory
import sfedu.ictis.walkOfInterest.domain.repository.CategoryRepository

class GetAllCategoriesUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(lang: String = "ru"): Result<List<DomainPickCategory>> =
        repository.getAllCategories(lang)
}
