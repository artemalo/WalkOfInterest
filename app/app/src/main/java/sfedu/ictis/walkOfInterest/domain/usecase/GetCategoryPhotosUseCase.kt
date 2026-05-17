package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.repository.CategoryRepository

class GetCategoryPhotosUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(): Result<Map<Int, String>> = repository.getCategoryPhotos()
}
