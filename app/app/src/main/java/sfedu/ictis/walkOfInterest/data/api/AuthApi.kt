package sfedu.ictis.walkOfInterest.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import sfedu.ictis.walkOfInterest.data.model.AuthResponse
import sfedu.ictis.walkOfInterest.data.model.LoginRequest
import sfedu.ictis.walkOfInterest.data.model.LogoutRequest
import sfedu.ictis.walkOfInterest.data.model.RefreshRequest
import sfedu.ictis.walkOfInterest.data.model.RegisterRequest

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): AuthResponse


    @POST("api/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<Unit>


    @POST("api/auth/logout-all")
    suspend fun logoutAll(@Header("Authorization") token: String): Response<Unit>
}