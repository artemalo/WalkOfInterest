package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory

interface CategoryRepository {
    suspend fun getAllCategories(lang: String = "ru"): Result<List<DomainPickCategory>>
}
