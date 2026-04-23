package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.data.api.AuthApi
import sfedu.ictis.walkOfInterest.data.model.AuthResponse
import sfedu.ictis.walkOfInterest.data.model.LoginRequest
import sfedu.ictis.walkOfInterest.data.model.RegisterRequest
import sfedu.ictis.walkOfInterest.domain.repository.AuthRepository

class AuthRepositoryImpl(private val api: AuthApi) : AuthRepository {
    override suspend fun login(request: LoginRequest): Result<AuthResponse> = runCatching {
        api.login(request)
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> = runCatching {
        api.register(request)
    }
}