package sfedu.ictis.walkOfInterest.domain.repository

import kotlinx.coroutines.flow.Flow
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile

interface UserRepository {
    suspend fun getMyProfile(): Result<DomainUserProfile>
    suspend fun getProfileByUsername(username: String): Result<DomainUserProfile>
    suspend fun getReviewsByUsername(username: String): Result<List<DomainReview>>

    suspend fun updateNickname(newUsername: String): Result<DomainUserProfile>
    suspend fun updateProfileInfo(firstName: String, lastName: String, bio: String): Result<DomainUserProfile>

    fun observeProfile(): Flow<DomainUserProfile?>
}