package sfedu.ictis.walkOfInterest.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import sfedu.ictis.walkOfInterest.data.model.UpdateProfileRequest
import sfedu.ictis.walkOfInterest.data.model.UpdateUsernameRequest
import sfedu.ictis.walkOfInterest.data.model.dto.ReviewDto
import sfedu.ictis.walkOfInterest.data.model.dto.UserProfileDto

interface UserApi {
    @GET("api/users/me")
    suspend fun getMyProfile(): Response<UserProfileDto>

    @GET("api/users/{username}")
    suspend fun getProfileByUsername(
        @Path("username") username: String
    ): Response<UserProfileDto>

    @GET("api/users/{username}/reviews")
    suspend fun getReviewsByUsername(
        @Path("username") username: String,
        @Query("lang") lang: String = "ru"
    ): Response<List<ReviewDto>>

    @PATCH("api/users/me")
    suspend fun updateUsername(
        @Body request: UpdateUsernameRequest
    ): Response<UserProfileDto>

    @PATCH("api/users/me/info")
    suspend fun updateProfileInfo(
        @Body request: UpdateProfileRequest
    ): Response<UserProfileDto>

    @POST("api/users/me/increment-trips")
    suspend fun incrementTrips(): Response<Void>
}