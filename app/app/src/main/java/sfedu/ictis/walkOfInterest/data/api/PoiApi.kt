package sfedu.ictis.walkOfInterest.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import sfedu.ictis.walkOfInterest.data.model.dto.PoiInfoDto
import sfedu.ictis.walkOfInterest.data.model.dto.ReviewDto

interface PoiApi {
    @GET("api/pois/{id}")
    suspend fun getPoiById(
        @Path("id") id: Long,
        @Query("lang") lang: String = "ru"
    ): Response<PoiInfoDto>

    @GET("api/pois/{id}/reviews")
    suspend fun getReviewsByPoiId(
        @Path("id") id: Long,
        @Query("lang") lang: String = "ru"
    ): Response<List<ReviewDto>>
}