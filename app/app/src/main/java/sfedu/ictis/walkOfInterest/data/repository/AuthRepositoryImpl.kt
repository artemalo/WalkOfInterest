package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.data.api.AuthApi
import sfedu.ictis.walkOfInterest.data.local.TokenStorage
import sfedu.ictis.walkOfInterest.data.model.AuthResponse
import sfedu.ictis.walkOfInterest.data.model.LoginRequest
import sfedu.ictis.walkOfInterest.data.model.RegisterRequest
import sfedu.ictis.walkOfInterest.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    override suspend fun login(request: LoginRequest): Result<AuthResponse> = runCatching {
        val response = api.login(request)
        tokenStorage.saveTokens(response.accessToken, response.refreshToken)
        response
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> = runCatching {
        val response = api.register(request)
        tokenStorage.saveTokens(response.accessToken, response.refreshToken)
        response
    }
}