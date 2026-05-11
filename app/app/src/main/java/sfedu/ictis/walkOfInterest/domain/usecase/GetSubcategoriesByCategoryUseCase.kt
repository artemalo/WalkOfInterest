package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainSubcategoryPage
import sfedu.ictis.walkOfInterest.domain.repository.CategoryRepository

class GetSubcategoriesByCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(
        categoryId: Int,
        search: String? = null,
        page: Int = 0
    ): Result<DomainSubcategoryPage> =
        repository.getSubcategoriesByCategory(categoryId, search, page)
}