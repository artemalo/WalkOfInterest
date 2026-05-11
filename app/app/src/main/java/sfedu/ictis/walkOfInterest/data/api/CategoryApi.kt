package sfedu.ictis.walkOfInterest.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import sfedu.ictis.walkOfInterest.data.model.dto.CategoryWithSubcategoriesDto
import sfedu.ictis.walkOfInterest.data.model.dto.PageResponseDto
import sfedu.ictis.walkOfInterest.data.model.dto.SubcategoryShortDto

interface CategoryApi {
    @GET("api/categories")
    suspend fun getAllCategories(
        @Query("lang") lang: String = "ru"
    ): Response<List<CategoryWithSubcategoriesDto>>

    @GET("api/categories/{id}/subcategories")
    suspend fun getSubcategoriesByCategory(
        @Path("id")    categoryId: Int,
        @Query("search") search: String? = null,
        @Query("page")   page: Int = 0,
        @Query("size")   size: Int = 20
    ): Response<PageResponseDto<SubcategoryShortDto>>
}
