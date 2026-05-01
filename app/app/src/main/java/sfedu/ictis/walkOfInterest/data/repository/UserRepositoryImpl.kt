package sfedu.ictis.walkOfInterest.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    private val _profile = MutableStateFlow<DomainUserProfile?>(MOCK_PROFILE)

    override fun observeProfile(): Flow<DomainUserProfile?> = _profile.asStateFlow()

    override suspend fun getMyProfile(): Result<DomainUserProfile> = runCatching {
        delay(150)
        _profile.value ?: MOCK_PROFILE.also { _profile.value = it }
    }

    override suspend fun getReviewsByUsername(username: String): Result<List<DomainReview>> = runCatching {
        delay(200)
        MOCK_REVIEWS.filter { it.authorUsername == username }
    }

    override suspend fun updateNickname(newUsername: String): Result<DomainUserProfile> = runCatching {
        delay(200)
        val current = _profile.value ?: MOCK_PROFILE

        if (newUsername.equals("admin", ignoreCase = true)) {
            throw Exception("Никнейм уже занят")
        }

        val updated = current.copy(username = newUsername)
        _profile.value = updated

        MOCK_REVIEWS = MOCK_REVIEWS.map { r ->
            if (r.authorUsername == current.username) r.copy(authorUsername = newUsername) else r
        }

        updated
    }

    private companion object {
        val MOCK_PROFILE = DomainUserProfile(
            id = "u-1",
            username = "walker_42",
            firstName = "Иван",
            lastName = "Иванов",
            bio = "Люблю гулять по городу и открывать новые места",
            photoUrl = null,
            countTrips = 12,
            countSpots = 5,
            countComments = 3
        )

        var MOCK_REVIEWS = listOf(
            DomainReview(
                id = "r-1",
                authorUsername = "walker_42",
                authorAvatarUrl = null,
                poiId = "poi-1",
                poiName = "Парк Горького",
                content = "Это милое местечко! Немного людно, но вид потрясающий.",
                rating = 5,
                likes = 12,
                dislikes = 1,
                createdAtMillis = System.currentTimeMillis() - 86_400_000L * 2
            ),
            DomainReview(
                id = "r-2",
                authorUsername = "walker_42",
                authorAvatarUrl = null,
                poiId = "poi-2",
                poiName = "Третьяковская галерея",
                content = "Внутри много интересного, но очереди длинные.",
                rating = 4,
                likes = 8,
                dislikes = 2,
                createdAtMillis = System.currentTimeMillis() - 86_400_000L * 14
            ),
            DomainReview(
                id = "r-3",
                authorUsername = "walker_42",
                authorAvatarUrl = null,
                poiId = "poi-3",
                poiName = "ВДНХ",
                content = "Большая территория, удобно гулять с детьми.",
                rating = 5,
                likes = 24,
                dislikes = 0,
                createdAtMillis = System.currentTimeMillis() - 86_400_000L * 30
            )
        )
    }
}