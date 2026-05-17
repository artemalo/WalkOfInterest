package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainSubcategoryPage

interface CategoryRepository {
    suspend fun getAllCategories(lang: String = "ru"): Result<List<DomainPickCategory>>

    suspend fun getCategoryPhotos(): Result<Map<Int, String>>

    suspend fun getSubcategoriesByCategory(
        categoryId: Int,
        search: String? = null,
        page: Int = 0
    ): Result<DomainSubcategoryPage>
}
