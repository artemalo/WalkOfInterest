package sfedu.ictis.walkOfInterest.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import sfedu.ictis.walkOfInterest.data.model.dto.PoiInfoDto
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
}