package sfedu.ictis.walkOfInterest.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import retrofit2.Response
import sfedu.ictis.walkOfInterest.data.api.UserApi
import sfedu.ictis.walkOfInterest.data.mapper.toDomain
import sfedu.ictis.walkOfInterest.data.model.UpdateUsernameRequest
import sfedu.ictis.walkOfInterest.domain.exception.ServerException
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.domain.repository.UserRepository

class UserRepositoryImpl(
    private val api: UserApi
) : UserRepository {
    private val _profile = MutableStateFlow<DomainUserProfile?>(null)

    override fun observeProfile(): Flow<DomainUserProfile?> = _profile.asStateFlow()

    override suspend fun getMyProfile(): Result<DomainUserProfile> = runCatching {
        val response = api.getMyProfile()
        val body = response.body()

        if (response.isSuccessful && body != null) {
            val domain = body.toDomain()
            _profile.value = domain
            domain
        } else {
            throw response.toException("Не удалось загрузить профиль")
        }
    }

    override suspend fun getReviewsByUsername(username: String): Result<List<DomainReview>> = runCatching {
        val response = api.getReviewsByUsername(username)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            body.map { it.toDomain() }
        } else {
            throw response.toException("Не удалось загрузить отзывы")
        }
    }

    override suspend fun updateNickname(newUsername: String): Result<DomainUserProfile> = runCatching {
        val response = api.updateUsername(UpdateUsernameRequest(newUsername))
        val body = response.body()

        if (response.isSuccessful && body != null) {
            val domain = body.toDomain()
            _profile.value = domain
            domain
        } else {
            throw response.toException("Не удалось обновить никнейм")
        }
    }



    private fun Response<*>.toException(fallback: String): Exception {
        val rawError = errorBody()?.string()
        val parsedMessage = rawError?.let { tryParseFirstJsonValue(it) }

        return when (code()) {
            in 400..499 -> ServerException(parsedMessage ?: fallback)
            else -> Exception("$fallback (код ${code()})")
        }
    }

    private fun tryParseFirstJsonValue(raw: String): String? = runCatching {
        val json = JSONObject(raw)
        val keys = json.keys()
        if (keys.hasNext()) json.getString(keys.next()) else null
    }.getOrNull()
}