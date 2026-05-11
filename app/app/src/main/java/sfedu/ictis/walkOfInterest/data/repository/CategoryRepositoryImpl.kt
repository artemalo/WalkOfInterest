package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.data.api.CategoryApi
import sfedu.ictis.walkOfInterest.data.mapper.toDomainOrNull
import sfedu.ictis.walkOfInterest.data.util.toException
import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPickSubcategory
import sfedu.ictis.walkOfInterest.domain.model.DomainSubcategoryPage
import sfedu.ictis.walkOfInterest.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val api: CategoryApi
) : CategoryRepository {

    override suspend fun getAllCategories(lang: String): Result<List<DomainPickCategory>> = runCatching {
        val response = api.getAllCategories(lang)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            body.mapNotNull { it.toDomainOrNull() }
        } else {
            throw response.toException("Не удалось загрузить список категорий")
        }
    }

    override suspend fun getSubcategoriesByCategory(
        categoryId: Int,
        search: String?,
        page: Int
    ): Result<DomainSubcategoryPage> = runCatching {
        val response = api.getSubcategoriesByCategory(categoryId, search, page)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            val items = body.content.orEmpty().mapNotNull { dto ->
                val name = dto.subcategoryName?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                DomainPickSubcategory(id = dto.subcategoryId, name = name, categoryId = categoryId)
            }
            DomainSubcategoryPage(
                content   = items,
                page      = body.page,
                totalPages = body.totalPages,
                isLast    = body.last
            )
        } else {
            throw response.toException("Не удалось загрузить подкатегории")
        }
    }
}