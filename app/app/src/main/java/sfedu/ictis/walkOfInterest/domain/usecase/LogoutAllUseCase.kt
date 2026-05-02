package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.repository.AuthRepository

class LogoutAllUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.logoutAll()
}