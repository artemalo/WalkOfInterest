package sfedu.ictis.walkOfInterest.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import sfedu.ictis.walkOfInterest.data.model.dto.PoiAddDto
import sfedu.ictis.walkOfInterest.data.model.dto.PoiCardDto
import sfedu.ictis.walkOfInterest.data.model.dto.PoiInfoDto
import sfedu.ictis.walkOfInterest.data.model.dto.PoiNearbyCheckRequestDto
import sfedu.ictis.walkOfInterest.data.model.dto.PoiNearbyCheckResponseDto
import sfedu.ictis.walkOfInterest.data.model.dto.ReviewDto
import sfedu.ictis.walkOfInterest.data.model.dto.ReviewReactionRequestDto
import sfedu.ictis.walkOfInterest.data.model.dto.ReviewReactionResponseDto
import sfedu.ictis.walkOfInterest.data.model.dto.ReviewRequestDto

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

    @PUT("api/pois/{id}/reviews/me")
    suspend fun upsertMyReview(
        @Path("id") id: Long,
        @Body request: ReviewRequestDto,
        @Query("lang") lang: String = "ru"
    ): Response<ReviewDto>

    @PUT("api/reviews/{reviewId}/reaction")
    suspend fun setReviewReaction(
        @Path("reviewId") reviewId: Long,
        @Body request: ReviewReactionRequestDto
    ): Response<ReviewReactionResponseDto>

    // ===== Add-POI feature =====

    @POST("api/pois/check")
    suspend fun checkNearby(
        @Body request: PoiNearbyCheckRequestDto,
        @Query("lang") lang: String = "ru"
    ): Response<PoiNearbyCheckResponseDto>

    @POST("api/pois")
    suspend fun createPoi(
        @Body poi: PoiAddDto
    ): Response<PoiInfoDto>

    @PUT("api/pois/{id}")
    suspend fun updatePoi(
        @Path("id") id: Long,
        @Body poi: PoiAddDto
    ): Response<PoiInfoDto>

    @POST("api/pois/{id}/supplement")
    suspend fun supplementPoi(
        @Path("id") id: Long,
        @Body poi: PoiAddDto
    ): Response<PoiInfoDto>

    @GET("api/pois/my")
    suspend fun getMyPois(
        @Query("lang") lang: String = "ru"
    ): Response<List<PoiCardDto>>
}
