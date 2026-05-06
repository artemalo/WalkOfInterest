package sfedu.ictis.walkOfInterest.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import sfedu.ictis.walkOfInterest.data.model.dto.CategoryWithSubcategoriesDto

interface CategoryApi {
    @GET("api/categories")
    suspend fun getAllCategories(
        @Query("lang") lang: String = "ru"
    ): Response<List<CategoryWithSubcategoriesDto>>
}
