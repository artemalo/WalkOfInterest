package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.data.api.AuthApi
import sfedu.ictis.walkOfInterest.data.local.TokenStorage
import sfedu.ictis.walkOfInterest.data.model.AuthResponse
import sfedu.ictis.walkOfInterest.data.model.LoginRequest
import sfedu.ictis.walkOfInterest.data.model.LogoutRequest
import sfedu.ictis.walkOfInterest.data.model.RegisterRequest
import sfedu.ictis.walkOfInterest.domain.repository.AuthRepository
import sfedu.ictis.walkOfInterest.utils.SessionManager

class AuthRepositoryImpl(
    /**
     * Идёт через auth_client (без AuthInterceptor / TokenAuthenticator)
     * для login/register/refresh, чтобы не было рекурсивного refresh
     */
    private val authApi: AuthApi,
    /**
     * Идёт через protected_client (AuthInterceptor + TokenAuthenticator)
     * для logout / logout-all, которым нужен access-токен
     */
    private val protectedAuthApi: AuthApi,

    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<AuthResponse> = runCatching {
        val response = authApi.login(request)
        tokenStorage.saveTokens(response.accessToken, response.refreshToken)
        response
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> = runCatching {
        val response = authApi.register(request)
        tokenStorage.saveTokens(response.accessToken, response.refreshToken)
        response
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        val refreshToken = tokenStorage.getRefreshToken()

        try {
            if (!refreshToken.isNullOrBlank()) {
                protectedAuthApi.logout(LogoutRequest(refreshToken))
            }
        } catch (_: Exception) {

        } finally {
            tokenStorage.clear()
            sessionManager.triggerLogout()
        }
    }

    override suspend fun logoutAll(): Result<Unit> = runCatching {
        try {
            protectedAuthApi.logoutAll()
        } catch (_: Exception) {

        } finally {
            tokenStorage.clear()
            sessionManager.triggerLogout()
        }
    }
}