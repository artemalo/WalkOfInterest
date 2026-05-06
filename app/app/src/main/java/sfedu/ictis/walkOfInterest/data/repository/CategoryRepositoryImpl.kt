package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.data.api.CategoryApi
import sfedu.ictis.walkOfInterest.data.mapper.toDomainOrNull
import sfedu.ictis.walkOfInterest.data.util.toException
import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory
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
}